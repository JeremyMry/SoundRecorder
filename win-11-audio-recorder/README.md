# Windows 11 Audio Recorder

## What is this ?

This is a Windows 11 C++ audio recorder that captures all audio output from your computer and saves it a PCM file.

## Pre requisites

Run on Windows 11 with Visual Studio 2022.

## How to use it ?

- create a solution in Visual Studio 2022
- run the project, it will start recording for a hardcoded duration specified in `main.cpp`
- an `output.pcm` file then be saved in the same folder, this is the file you want to convert as `WAV` (using `Audacity` for instance)
