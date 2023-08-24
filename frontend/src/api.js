import axios from "axios";
import { getItemFromLocalStorage } from "./utils/helpers";

const instance = axios.create({
  baseURL: process.env.REACT_APP_REST_SERVER_URL,
  headers: {
    //"Access-Control-Allow-Origin": "*",
  },
});

instance.interceptors.request.use((config) => {
  if (getItemFromLocalStorage("token")) {
    config.headers["Authorization"] = `Bearer ${getItemFromLocalStorage(
      "token"
    )}`;
  }
  return config;
});

export default instance;
