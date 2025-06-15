import express from "express";

const app = express();
const port = 3000; // 你可以自定义端口

app.get(
  "/login",  (    req:any,    res :any  ) => {
    const { uname, pwd } = req.query;
    //get dto

    if (!uname || !pwd) {
      return res.status(400).json({ error: "用户名或密码缺失" });
    }

    // 这里可以添加你的验证逻辑
    console.log(`Login attempt: uname=${uname}, pwd=${pwd}`);

    res.json({ message: "登录成功", username: uname });
  }
);

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
