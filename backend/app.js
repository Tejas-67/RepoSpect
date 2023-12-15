const express = require('express');
const app = express();
const mongoose = require('mongoose');
const User = require('./models/user');
require('dotenv').config();

const dbUri = process.env.DB_URI;

mongoose.connect(dbUri)
    .then((result)=>{
        app.listen(3000);
        console.log('connected and listening');
    })
    .catch((err)=> console.log(err));

app.get('/allusers', (req, res)=>{
    User.find()
        .then((result)=>{
            res.status(200).send(result);
        })
        .catch((err)=>{
            console.log(err);
        })
});

app.get('/login', (req, res)=>{
    const query = {email: req.query.email, password: req.query.password}

    User.findOne(query)
        .then((result)=>{
            if(result==null){
                res.status(302).json({message: "User doesn't exist"});
            }
            else{
                res.status(200).json(result);
            }
        })
        .catch((err)=>{
            res.send(400).json(err);
        })
});

app.get('/signup', (req, res)=>{
    const user = new User({
        name: req.query.name,
        email: req.query.email,
        password: req.query.password,
        image: req.query.image
    });

    const query = {email: user.email};
    User.findOne(query)
        .then((result)=>{
            if(result==null){
                user.save()
                    .then((result)=>{
                        res.status(200).json(result);
                    })
            }
            else{
                res.status(302).json({message: "Email already exists"});
            }
        })
        .catch((err)=>{
            res.status(400).json(err);
        })
});

app.post('/update-profile-picture', (req, res)=>{
    const email = req.query.email;
    const newImageUrl = req.query.imageUrl;
    
    const query = {email: email};
    User.findOne(query)
        .then((result)=>{
            if(result==null){
                res.status(302).json({message: "Couldn't find user"});
            }
            else{
                result.image = newImageUrl;
                result.save();
                res.status(200).json(result);
            }
        })
        .catch((err)=>{
            res.status(400).json(err);
        })
});

app.get('/update-password', (req, res)=>{
    const email = req.query.email;
    const currentPassword = req.query.currentPassword;
    const newPassword = req.query.newPassword;

    const query = {email: email, password: currentPassword};
    User.findOne(query)
        .then((result)=>{
            if(result==null) {
                res.status(302).json({message: "Invalid email or password"});
            }
            else{
                result.password = newPassword;
                result.save();
                res.status(200).json(result);
            }
        })
        .catch((err)=>{
            res.status(400).json(err);
        })
});

app.post('/update-username', (req, res)=>{
    const email = req.query.email;
    const newUsername = req.query.newUsername;
    const password = req.query.password;
    const query = {email: email, password: password};
    User.findOne(query)
        .then((result) => {
            if(result==null){
                res.status(302).json({message: "Couldn't find user"});
            }
            else{
                result.name=newUsername;
                result.save();
                res.status(200).json(result);
            }
        })
        .catch((err)=>{
            res.status(400).json(err);
        })
});