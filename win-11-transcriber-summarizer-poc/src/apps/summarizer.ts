import fs from 'fs';
import OpenAI from 'openai';
import path from 'path';

require('dotenv').config();

const inputFolder = path.join(__dirname, '..', '..', 'out', 'subtranscripts');
const outputFolder = path.join(__dirname, '..', '..', 'out', 'transcripts');

const openai = new OpenAI({
    apiKey: process.env.OPENAI_API_KEY
});

// TODO run summarization periodically and losslessly
const summarize = async (transcript: string) => {
    const summary = await openai.chat.completions.create({
        messages: [
            {
                role: "system",
                content: `You're a transcriber that outputs information in Markdown format. You will extract the information from the input unstructured transcript in the following format`
            },
            {
                role: "system",
                content: `
                # [Title]
            
                ## [Subtitle]
    
                ## [Action]
    
                ## Overall tone: [Tone]
    
                ## Overall sentiment: [Sentiment]
    
                ## Overall emotion conveyed: [Emotion]
                `
            },
            {
                role: "system",
                content: "There will be as many subtitles as there are key points in the input transcript."
            },
            {
                role: "system",
                content: "There will be as many actions as there are agreed upon or mentioned actions to be taken in the input transcript."
            },
            {
                role: "system",
                content: "There will be no actions if there are no actions to be taken in the input transcript."
            },
            {
                role: "system",
                content: "If an action is assigned to an actor, please specify the actor before the action in capital letters, followed by a semicolon, followed by the action."
            },
            {
                role: "system",
                content: "Each action will be specified with a short sentence below its subtitle."
            },
            {
                role: "system",
                content: "Describe the overall tone under the unique overall tone subtitle."
            },
            {
                role: "system",
                content: "[Sentiment] can be 'positive', 'negative' or 'neutral'."
            },
            {
                role: "system",
                content: "Please provide a short explanation for the sentiment under its subtitle."
            },
            {
                role: "system",
                content: "[Emotion] is a one word adjective."
            },
            {
                role: "system",
                content: "Please provide a short explanation for the emotion under its subtitle."
            },
            {
                content: transcript,
                role: "user"
            }
        ],
        model: "gpt-3.5-turbo-16k-0613"
    });
    return summary;
};

(async () => {

    // keeping previous transcripts if any
    let count = 0;
    let transcript = path.join(
        outputFolder, `transcript${count == 0 ? '': count}.md`);
    if (!fs.existsSync(transcript)) {
        fs.writeFileSync(transcript, '');
    } else {
        while(fs.existsSync(transcript))  {
            transcript = path.join(
                outputFolder, `transcript${count++}.md`);
            count++;
        }
    }

    // checking if there are files to transcribe
    let subTranscripts: string[] = [];
    try {
        subTranscripts = fs.readdirSync(inputFolder)
            .filter(file => 
                path.extname(file).toLowerCase() === ".md"
            );
    } catch (error) {
        console.error(error);
    }
    subTranscripts.sort();
    console.log(`Found ${subTranscripts.length} files to transcribe...`);

    // transcribing files
    console.log(`Transcribing files...`);
    let subTranscriptsContents = '';
    for (const [index, subTranscript] of subTranscripts.entries()) {
        // getting the sub transcript
        subTranscriptsContents +=`\n${fs.readFileSync(path.join(inputFolder, subTranscript), 'utf8')}\n`;

        // delete sub transcript file after processing
        fs.unlinkSync(path.join(inputFolder, subTranscript));
    }

    if (subTranscriptsContents.trim() !== '') {
        // preparing the contents to summarize
        const toSummarize = subTranscriptsContents;

        // calling the summarizer
        const summary = await summarize(toSummarize);

        // writing the summary to the transcript file
        if (summary.choices[0].message.content &&
            summary.choices[0].message.content?.trim() != '') {
            fs.writeFileSync(transcript, summary.choices[0].message.content!);
        }

    }

})();