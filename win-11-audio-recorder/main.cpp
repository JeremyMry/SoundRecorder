// setup steps: right click project -> properties -> configuration properties -> linker -> input -> additional dependencies -> add "ole32.lib" and "avrt.lib"
// these libs are required for COM and WASAPI (Windows Core Audio APIs) respectively 

#include <Windows.h>
#include <Audioclient.h>

// interfaces and data structures for Windows and multimedia devices (part of the Windows Core Audio API)
#include <Mmdeviceapi.h>

// contains declarations of property keys used to access the properties of an audio device
#include <Functiondiscoverykeys_devpkey.h>

// standard I/O declarations and functions for C
#include <stdio.h>

#include <chrono>
#include <string>
#include <iostream>
#include <filesystem>


int main() {

    // initialize COM
    HRESULT hr = CoInitializeEx(NULL, COINIT_MULTITHREADED);
    if (FAILED(hr)) {
        // Handle error
        return -1;
    }

    // getting the default audio endpoint with an enumerator
    IMMDeviceEnumerator* pEnumerator = NULL;
    IMMDevice* pDevice = NULL;
    // getting a pointer to a device enumerator 
    hr = CoCreateInstance(__uuidof(MMDeviceEnumerator), NULL, CLSCTX_ALL, __uuidof(IMMDeviceEnumerator), (void**)&pEnumerator);
    if (FAILED(hr)) {
        // Handle error
        CoUninitialize();
        return -1;
    }
    hr = pEnumerator->GetDefaultAudioEndpoint(eRender, eConsole, &pDevice);
    pEnumerator->Release();
    if (FAILED(hr)) {
        // Handle error
        CoUninitialize();
        return -1;
    }

    // verifying that we have a valid device
    IPropertyStore* pProps = NULL;
    hr = pDevice->OpenPropertyStore(STGM_READ, &pProps);
    if (SUCCEEDED(hr)) {
        PROPVARIANT varName;
        PropVariantInit(&varName);

        hr = pProps->GetValue(PKEY_Device_FriendlyName, &varName);
        if (SUCCEEDED(hr)) {
            wprintf(L"Default Audio Endpoint: %s\n", varName.pwszVal);
        }
        PropVariantClear(&varName);
        pProps->Release();
    }
    else {
        wprintf(L"No Default Audio Endpoint Found\n");
        // Handle error
        CoUninitialize();
        return -1;
    }

    // activate an audio client
    IAudioClient* pAudioClient = NULL;
    hr = pDevice->Activate(__uuidof(IAudioClient), CLSCTX_ALL, NULL, (void**)&pAudioClient);
    if (FAILED(hr)) {
        // Handle error
        pDevice->Release();
        CoUninitialize();
        return -1;
    }

    // set the desired audio format
    WAVEFORMATEX* pwfx = NULL; // wave format structure
    hr = pAudioClient->GetMixFormat(&pwfx); // asking the question "In what format are you processing audio?" => this ensures that the audio format is supported and is a high quality capture
    if (FAILED(hr)) {
        // Handle error
        pAudioClient->Release();
        pDevice->Release();
        CoUninitialize();
        return -1;
    }

    // initializing the audio stream
    DWORD streamFlags = AUDCLNT_STREAMFLAGS_LOOPBACK; // flag that allows us to capture the audio that's being played by the audio endpoint device
    hr = pAudioClient->Initialize(AUDCLNT_SHAREMODE_SHARED, streamFlags, 0, 0, pwfx, NULL); // the shared mode means that the audio engine will share the audio endpoint device with other applications
    if (FAILED(hr)) {
        // Handle error
        CoTaskMemFree(pwfx);  // Free the memory allocated by GetMixFormat
        pAudioClient->Release();
        pDevice->Release();
        CoUninitialize();
        return -1;
    }

    // get the capture client interface
    IAudioCaptureClient* pCaptureClient = NULL;
    hr = pAudioClient->GetService(__uuidof(IAudioCaptureClient), (void**)&pCaptureClient);
    if (FAILED(hr)) {
        // Handle error
        pAudioClient->Release();
        pDevice->Release();
        CoUninitialize();
        return -1;
    }

    // Calculate buffer size and allocate buffer for the duration of the recording
    // TODO make duration configurable
    DWORD duration = 8; // average duration of an English sentence
    DWORD bufferSize = duration * pwfx->nSamplesPerSec * (pwfx->wBitsPerSample / 8) * pwfx->nChannels; // formula for calculating the buffer size (in bytes) = duration * sample rate * (bits per sample / 8) * number of channels
    BYTE* dataBuffer = new BYTE[bufferSize];
    DWORD bufferOffset = 0;

    // Start Capturing audio
    hr = pAudioClient->Start();
    if (FAILED(hr)) {
        // Handle error
        delete[] dataBuffer;
        pCaptureClient->Release();
        CoTaskMemFree(pwfx);
        pAudioClient->Release();
        pDevice->Release();
        CoUninitialize();
        return -1;
    }

    // Retrieve captured audio data for duration
    auto startTime = std::chrono::steady_clock::now();
    auto endTime = startTime + std::chrono::seconds(duration - 1);
    UINT32 packetLength = 0;
    hr = pCaptureClient->GetNextPacketSize(&packetLength);
    while (std::chrono::steady_clock::now() < endTime) {
        BYTE* pData;
        UINT32 numFramesAvailable;
        DWORD flags;
        if (bufferOffset >= bufferSize) {
            wprintf(L"Buffer overflow detected.\n");
            delete[] dataBuffer;
            pCaptureClient->Release();
            CoTaskMemFree(pwfx);
            pAudioClient->Release();
            pDevice->Release();
            CoUninitialize();
            return -1;
        }
        hr = pCaptureClient->GetBuffer(&pData, &numFramesAvailable, &flags, NULL, NULL);
        if (SUCCEEDED(hr)) {
            DWORD bytesToCopy = numFramesAvailable * pwfx->nBlockAlign;
            if (bufferOffset + bytesToCopy <= bufferSize) {
                memcpy(dataBuffer + bufferOffset, pData, bytesToCopy);
                bufferOffset += bytesToCopy;
            }
            else {
                wprintf(L"Buffer size exceeded while capturing audio.\n");
                delete[] dataBuffer;
                pCaptureClient->Release();
                CoTaskMemFree(pwfx);
                pAudioClient->Release();
                pDevice->Release();
                CoUninitialize();
                return -1;
            }
            hr = pCaptureClient->ReleaseBuffer(numFramesAvailable);
        }
        else {
            wprintf(L"Failed to get buffer for audio data.\n");
        }
        hr = pCaptureClient->GetNextPacketSize(&packetLength);
    }

    // Stop capturing
    hr = pAudioClient->Stop();
    if (FAILED(hr)) {
        // Handle error
        pCaptureClient->Release();
        pAudioClient->Release();
        pDevice->Release();
        CoUninitialize();
        return -1;
    }

    // getting the output audio file path
    auto filePath = std::filesystem::current_path().append("out").append("raw").append("output.pcm");
    auto count = 0;
    bool fileExists = std::filesystem::exists(filePath.c_str());
    while (fileExists) {
        filePath = std::filesystem::current_path().append("out").append("raw").append("output" + std::to_string(count) + ".pcm");
        fileExists = std::filesystem::exists(filePath.c_str());
        count++;
    }

    // Write to PCM file
    FILE* pFile;
    errno_t err = _wfopen_s(&pFile, filePath.c_str(), L"wb");  // open the file in binary mode and overwrite the file if it already exists
    if (pFile) {
        // Write the audio data
        fwrite(dataBuffer, bufferOffset, 1, pFile);
        fclose(pFile);
    }
    if (err != 0 || !pFile) {
        // Handle error
        delete[] dataBuffer;
        pCaptureClient->Release();
        CoTaskMemFree(pwfx);
        pAudioClient->Release();
        pDevice->Release();
        CoUninitialize();
        return -1;
    }

    // Cleanup
    delete[] dataBuffer;
    pCaptureClient->Release();
    CoTaskMemFree(pwfx);
    pAudioClient->Release();
    pDevice->Release();
    CoUninitialize();
    return 0;

}