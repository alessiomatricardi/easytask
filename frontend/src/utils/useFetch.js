import { useState, useCallback } from "react";
import instance from "../api";

const useFetch = (url) => {
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [data, setData] = useState([]);

  const executeFetch = useCallback(
    // Here you will access to the latest updated options.
    async () => {
      setIsLoading(true);
      setError(null);
      try {
        const response = await instance.get(url);
        setData(response.data);
      } catch (err) {
        setError(err.response.data.message || "Something went bad");
      } finally {
        setIsLoading(false);
      }
    },
    [url, setIsLoading, setError]
  );
  return { data, error, isLoading, executeFetch };
};

export default useFetch;
