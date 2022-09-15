const express = require('express');
const app = express();
const mysql = require('mysql');

const {sequelize, Board, Review} = require('./models'); // 무슨 모델 쓸건지
const { Sequelize } = require('sequelize');

app.use(express.json()); // json 파싱을 위한 사용

sequelize.sync({force:false}) // DB와 연결하기
    .then(() =>{
        console.log('DB 연결 성공');
    })
    .catch((err) => {
        console.log("error!!!!");
        console.error(err);
});


app.post('/review', (req, res)=>{
    const newReview = {
        reviewSender:req.body.reviewSender,
        reviewReceiver:req.body.reviewReceiver,
        reviewContent:req.body.reviewContent,
        reviewDate:req.body.reviewDate,
        reviewRate:req.body.reviewRate,
    }

    console.log(newReview);

    Review.create(newReview).then((result)=>{
        return res.status(200).send();
    })
    .catch((err)=>{
        console.error(err);
    })
});

app.post('/post', (req, res)=>{ // 새 글 쓰고싶음
    const newPost = { // 클라이언트 단에서 받아온 걸로 정보 얻어서
        available:"true",
        userEmail:req.body.userEmail,
        postID:req.body.postID,
        postTitle:req.body.postTitle,
        postContent:req.body.postContent,
        postStartLatitude:req.body.postStartLatitude,
        postStartLongitude:req.body.postStartLongitude,
        postEndLatitude:req.body.postEndLatitude,
        postEndLongitude:req.body.postEndLongitude,
    }

    console.log(`글 작성자 이메일 : ${newPost.userEmail}`);
    // 그걸로 데이터베이스에 작성할거임
    Board.create(newPost)
    .then((result)=>{ 
    //    console.log("생성 성공"); // 글 잘 써졌으니까 200 신호 보내고 종료
        return res.status(200).send();
    })
    .catch((err)=>{ 
        console.error(err);
    })

});

app.post('/delete', (req, res)=>{

    console.log("req.body.postID" + req.body.postID);
    Board.update({available : "false"}, {where : {postID : req.body.postID}})
    .then(result => {
        //console.log(result);
        return res.status(200).send();
    })
    .catch(error => {
        console.err(error)
    });
});

app.post('/get', (req, res)=>{ // 클라이언트가 정보 요청

    Board.findOne({
        order: [
            ['id', 'DESC']
        ]
    }).then((result)=>{
        console.log(result.id);
        result.postID = result.id + 1;
        return res.status(200).send(JSON.stringify(result));
    })
    .catch();

    // Board.findOne({where : {id : 1}}) // 조건 붙여서 찾기 [id = 1]
    // .then((result)=>{
    //     const postInfo = { // 해당 조건에 맞는 것 찾아 객체로 만듬
    //         postTitle:result.postTitle,
    //         postContent:result.postContent,
    //         //postLocation:result.postLocation
    //     }
        
    //     console.log(postInfo);
    //     return res.status(200).send(JSON.stringify(postInfo)); // JSON 파싱해서 보냄
    // })
})

app.post('/getReview', (req, res)=>{

    console.log("Review Receview " + req.body.reviewReceiver);

    Review.findAll({where : {reviewReceiver : req.body.reviewReceiver}})
    .then((result =>{
        console.log(result);
        return res.status(200).send(JSON.stringify(result));
    }))
    .catch(err => {console.error(err)});
});



app.post('/getRequest', (req, res)=>{

    console.log("Requester : " + req.body.userEmail);

    Board.findAll({where : {userEmail : req.body.userEmail, available : "true"}})
    .then((result =>{
        console.log(result);
        return res.status(200).send(JSON.stringify(result));
    }))
    .catch(err => {console.error(err)});
});




app.post('/getAll', (req, res)=>{

    Board.findAll({ where : {available : "true"}})
    .then((result => {
        console.log(result);
        return res.status(200).send(JSON.stringify(result)); 
    }))
})

app.listen(3000, ()=>{ // 3000 포트에서 리슨 중
    console.log("Listening on port 3000");
})