# Windows 11 Transcriber Summarizer POC

## What is this ?

This NodeJS application is responsible for instrumenting a C++ application that records the audio output of your Windows 11 computer, it:

- starts recording
- stops recording on user input

Under the hood, it repeatedly calls the [C++ program](https://github.com/yactouat/YTranscribe) that rotates PCM files in chunks of 8 seconds.

## Pre-requisites

- run on Windows (tested on Windows 11)
- have NodeJS installed
- have `ffmpeg` installed and available in your `PATH`

## How to use it ?

- change the `wav` enconding conf in `src\apps\sound-processor.ts` to match your main audio output device (there's an issue to fix this to make it dynamic)
- open a Git Bash shell at the root of this project
- `npm install`
- `npm run start` (or `npm run dev` if you want to debug the TypeScript code)
- hit `ctrl + c` in the shell from which you ran the app' to stop the recording, sub transcripts will be output in the `out\subtranscripts` folder
- to summarize them in a formatted way, run `npm run summarize` (or `npm run summarize:dev`) if you want to debug the TypeScript code
