import 'package:flutter/material.dart';
import 'package:hoply/db/post_page.dart';
import 'package:hoply/db/hoply_database.dart';
import 'package:hoply/db/tables.dart';
import 'package:hoply/Login_page/signup_widget.dart';

class Signup extends StatefulWidget {
  final Users? user;

  const Signup({
    Key? key,
    this.user,
  }) : super(key: key);
  @override
  _SignupState createState() => _SignupState();
}

class _SignupState extends State<Signup> {
  final _formKey = GlobalKey<FormState>();
  late String userid;
  late String name;
  late String password;

  @override
  void initState() {
    super.initState();

    userid = widget.user?.userid ?? '';
    name = widget.user?.name ?? '';
    password = widget.user?.password ?? '';
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          actions: [buildButton()],
          title: Text("Signup Page"),
        ),
        body: Form(
          key: _formKey,
          child: SignupFormWidget(
            userid: userid,
            name: name,
            password: password,
            onChangedUserid: (userid) => setState(() => this.userid = userid),
            onChangedName: (name) => setState(() => this.name = name),
            onChangedPassword: (password) =>
                setState(() => this.password = password),
          ),
        ), // form
      );
  //Build

  Widget buildButton() {
    final isFormValid =
        userid.isNotEmpty && name.isNotEmpty && password.isNotEmpty;

    return Padding(
        padding: EdgeInsets.symmetric(vertical: 8, horizontal: 12),
        child: ElevatedButton(
            style: ElevatedButton.styleFrom(
              onPrimary: Colors.white,
              primary: isFormValid ? null : Colors.grey.shade700,
            ),
            onPressed: () async {
              final created = await addUser();
              if (created) {
                Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (_) => PostPage(
                              userid: userid,
                            )));
              }
            },
            child: Text(
              'Sign up',
              style: TextStyle(color: Colors.white, fontSize: 25),
            )));
  }

  Future<bool> addUser() async {
    final user = Users(
      userid: userid,
      password: password,
      name: name,
      timestamp: DateTime.now(),
    );
    try {
      await HoplyDatabase.instance.createUser(user);
      return true;
    } catch (e) {
      return false;
    }
  }
}//SignupFormWidget
