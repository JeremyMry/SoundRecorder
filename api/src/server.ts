import api from "./api";

api.listen(process.env.API_PORT || 8080, () => {
    console.info('Server starts', process.env.API_PORT || 8080);
});