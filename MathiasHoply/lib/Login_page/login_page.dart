import 'package:flutter/material.dart';
import 'package:hoply/db/post_page.dart';
import 'package:hoply/db/hoply_database.dart';
import 'package:hoply/db/tables.dart';
import 'login_widget.dart';

class Login extends StatefulWidget {
  final Users? user;

  const Login({
    Key? key,
    this.user,
  }) : super(key: key);
  @override
  _LoginState createState() => _LoginState();
}

class _LoginState extends State<Login> {
  final _formKey = GlobalKey<FormState>();
  late String userid;
  late String password;

  @override
  void initState() {
    super.initState();

    userid = widget.user?.userid ?? '';
    password = widget.user?.password ?? '';
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          actions: [buildButton()],
          title: Text("Login Page"),
        ),

        body: Form(
          key: _formKey,
          child: LoginFormWidget(
            userid: userid,
            password: password,
            onChangedUserid: (userid) => setState(() => this.userid = userid),
            onChangedPassword: (password) =>
                setState(() => this.password = password),
          ),
        ),

        // form
      );
  //Build

  Widget buildButton() {
    final isFormValid = userid.isNotEmpty && password.isNotEmpty;

    return Padding(
      padding: EdgeInsets.symmetric(vertical: 8, horizontal: 12),
      child: ElevatedButton(
        style: ElevatedButton.styleFrom(
          onPrimary: Colors.white,
          primary: isFormValid ? null : Colors.grey.shade700,
        ),
        onPressed: () async {
          if (isFormValid) {
            Users us = await findUser();
            if (us.password == this.password) {
              Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context_) => PostPage(userid: this.userid)));
            }
          }
          ;
        },
        child: Text(
          'Login',
          style: TextStyle(color: Colors.white, fontSize: 25),
        ),
      ),
    );
  }

  Future<Users> findUser() async {
    final dummyUser = Users(
        userid: 'userid',
        password: '',
        name: 'name',
        timestamp: DateTime.now());
    try {
      return await HoplyDatabase.instance.readUser(this.userid);
    } catch (e) {
      print(e);
      return dummyUser;
    }
  }
}
