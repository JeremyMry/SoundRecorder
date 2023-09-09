import { execSync } from 'child_process';
import fs from 'fs';
import path from 'path';

const inputFolder = path.join(__dirname, '..', '..', 'out', 'raw');
const outputFolder = path.join(__dirname, '..', '..', 'out', 'processed');

(async () => {

    // deleting previous files if any
    let inputFilesToDelete: string[] = [];
    try {
        inputFilesToDelete = fs.readdirSync(inputFolder)
            .filter(file => 
                path.extname(file).toLowerCase() === ".pcm"
            );
    } catch (error) {
        console.error(error);
    }
    console.log(`Deleting ${inputFilesToDelete.length} previous input files...`);
    for (const file of inputFilesToDelete) {
        fs.unlinkSync(path.join(inputFolder, file));
    }
    let outputFilesToDelete: string[] = [];
    try {
        outputFilesToDelete = fs.readdirSync(outputFolder)
            .filter(file => 
                path.extname(file).toLowerCase() === ".wav"
            );
    } catch (error) {
        console.error(error);
    }
    console.log(`Deleting ${outputFilesToDelete.length} previous output files...`);
    for (const file of outputFilesToDelete) {
        fs.unlinkSync(path.join(outputFolder, file));
    }

    while (true) {

        // wait 1 second at the beginning of each loop
        await new Promise(resolve => setTimeout(resolve, 1000));

        // checking if there are files to process
        let filesToProcess: string[] = [];
        try {
            filesToProcess = fs.readdirSync(inputFolder)
                .filter(file => 
                    path.extname(file).toLowerCase() === ".pcm"
                );
        } catch (error) {
            console.error(error);
        }
        console.log(`Found ${filesToProcess.length} files to process...`);

        // processing files
        console.log(`Processing files...`);
        for (const file of filesToProcess) {
            // convert to wav
            // TODO make the sample format and rate dynamic
            execSync(`ffmpeg -f f32le -ar 44100 -ac 2 -i ${path.join(inputFolder, file)} ${path.join(outputFolder, file)}.wav`);
        }
        console.log(`${filesToProcess.length} files processed...`);

    }

})();