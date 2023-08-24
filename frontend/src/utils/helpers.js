export const getItemFromLocalStorage = (key) => {
  const item = localStorage.getItem(key);
  if (item) {
    return JSON.parse(item);
  } else {
    return item;
  }
};

export const setLocalStorageItem = (key, item) => {
  if (typeof key == "string") {
    localStorage.setItem(key, JSON.stringify(item));
  }
};
