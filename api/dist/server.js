"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const api_1 = __importDefault(require("./api"));
api_1.default.listen(process.env.API_PORT || 8080, () => {
    console.info('Server starts', process.env.API_PORT || 8080);
});
