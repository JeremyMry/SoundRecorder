import express from 'express';
import { Request, Response, NextFunction } from 'express';

// TODO API specs
const api = express();

const handleErrors = (err: Error, req: Request, res: Response, next: NextFunction) => {
    const status = 500;
    const msg = 'Internal Server Error';
    const data = null;
    console.error(`Error: ${err.message}`);
    res.status(status).json({ msg, data });
};

// global middlewares
api.use(express.json());

// base route
api.get('/', async (req, res) => {
    res.status(200).json({
        msg: 'Welcome to the SoundRecorder API',
        data: ''
    });
});

api.get('/test', async (req, res) => {
    console.log(req.body);
    res.status(200).json({
        msg: 'testing sending audio file',
        data: ''
    });
});
// 500 and 404 handlers
api.all('*', (req, res, next) => {
    if (!res.headersSent) {
        res.status(404).json({
            msg: "Not found",
            data: ''
        });
    }
});
api.use(handleErrors);

export default api;