import {
    initializeApp
} from "https://www.gstatic.com/firebasejs/12.17.1/firebase-app.js";

import {
    getDatabase
} from "https://www.gstatic.com/firebasejs/12.17.1/firebase-database.js";

const firebaseConfig = {
    apiKey: "AIzaSyAbMnSNHSqz4eqIkiZeW2JEjPEAa9j2p0M",

    authDomain:
        "smarthomemonitoringsyste-59316.firebaseapp.com",

    databaseURL:
        "https://smarthomemonitoringsyste-59316-default-rtdb.asia-southeast1.firebasedatabase.app",

    projectId:
        "smarthomemonitoringsyste-59316",

    storageBucket:
        "smarthomemonitoringsyste-59316.firebasestorage.app",

    messagingSenderId:
        "866326633355",

    appId:
        "1:866326633355:web:679f9dce5a268acbc5ad8e"
};

const firebaseApp = initializeApp(firebaseConfig);

const database = getDatabase(firebaseApp);

export {
    firebaseApp,
    database
};