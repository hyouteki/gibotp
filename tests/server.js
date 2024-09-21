const express = require('express');
const bodyParser = require('body-parser');
const app = express();

// Middleware to parse JSON request bodies
app.use(bodyParser.json());

// Route to handle OTP submission
app.post('/receive_otp', (req, res) => {
    const { otp } = req.body;
    
    if (otp) {
        console.log(`Received OTP: ${otp}`);
        // Do something with the OTP, like save it to a database, log it, etc.
        
        // Send a response back to the client
        res.status(200).send({ message: "OTP received successfully!" });
    } else {
        res.status(400).send({ message: "No OTP provided" });
    }
});

// Start the server on port 3000
app.listen(3000, () => {
    console.log('Server is running on http://localhost:3000');
});
