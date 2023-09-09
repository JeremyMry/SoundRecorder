import { spawnSync } from 'child_process';

const record = async () => {
    console.log(`\nStarting Recording audio...`);
    // execute `bin\YTranscribe_recorder.exe` to record audio (Windows only)
    const bash = spawnSync('C:\\Program Files\\Git\\bin\\bash.exe', ['-c', './YTranscribe_recorder.exe']);
    console.log(`\nRecording audio finished.`);
};

export default record;