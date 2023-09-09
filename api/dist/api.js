"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
// TODO API specs
const api = (0, express_1.default)();
const handleErrors = (err, req, res, next) => {
    const status = 500;
    const msg = 'Internal Server Error';
    const data = null;
    console.error(`Error: ${err.message}`);
    res.status(status).json({ msg, data });
};
// global middlewares
api.use(express_1.default.json());
// base route
api.get('/', (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    res.status(200).json({
        msg: 'Welcome to the SoundRecorder API',
        data: ''
    });
}));
api.get('/test', (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    console.log(req.body);
    res.status(200).json({
        msg: 'testing sending audio file',
        data: ''
    });
}));
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
exports.default = api;
