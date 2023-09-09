import fs from 'fs';
import OpenAI, { toFile } from 'openai';
import path from 'path';

require('dotenv').config();

const inputFolder = path.join(__dirname, '..', '..', 'out', 'processed');
const subTranscriptsFolder = path.join(__dirname, '..', '..', 'out', 'subtranscripts');

const openai = new OpenAI({
    apiKey: process.env.OPENAI_API_KEY
});

(async () => {

    // deleting previous files if any
    let subTranscriptFilesToDelete: string[] = [];
    try {
        subTranscriptFilesToDelete = fs.readdirSync(subTranscriptsFolder)
            .filter(file => 
                path.extname(file).toLowerCase() === ".md"
            );
    } catch (error) {
        console.error(error);
    }
    console.log(`Deleting ${subTranscriptFilesToDelete.length} previous sub transcript files...`);
    for (const file of subTranscriptFilesToDelete) {
        fs.unlinkSync(path.join(subTranscriptsFolder, file));
    }

    while (true) {

        // wait 1 second at the beginning of each loop
        await new Promise(resolve => setTimeout(resolve, 1000));

        // checking if there are files to transcribe
        let wavToTranscribe: string[] = [];
        try {
            wavToTranscribe = fs.readdirSync(inputFolder)
                .filter(file => 
                    path.extname(file).toLowerCase() === ".wav"
                );
        } catch (error) {
            console.error(error);
        }
        console.log(`Found ${wavToTranscribe.length} files to transcribe...`);

        // transcribing files
        console.log(`Transcribing files...`);
        for await (const file of wavToTranscribe) {
            // converting audio transcript to a suitable format for OpenAI
            const toFiled = await toFile(fs.createReadStream(path.join(inputFolder, file)));
            try {
                // transcribing
                const transcript = await openai.audio.transcriptions.create({
                    file: toFiled,
                    model: "whisper-1",

                    // the language of the input audio
                    // language: "fr",

                    // optional text to guide the model's style or continue a previous audio segment
                    // prompt: "Transcript:",
                });

                // writing the sub transcript to a file
                const subTranscriptFileName = path.join(subTranscriptsFolder, file.replace('.wav', '.md'));
                fs.writeFileSync(subTranscriptFileName, transcript.text);

                // delete `wav`and sub transcript file after processing
                fs.unlinkSync(path.join(inputFolder, file));
                fs.unlinkSync(path.join(subTranscriptsFolder, subTranscriptFileName));
            } catch (error) {
                console.error((error as Error).message);
                console.error(`could not process ${file}`);
            }
        }
    }

})();