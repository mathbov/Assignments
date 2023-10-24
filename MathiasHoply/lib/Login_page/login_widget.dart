import 'package:flutter/material.dart';
import 'signup_page.dart';
import 'package:http/http.dart' as http;

class LoginFormWidget extends StatelessWidget {
  final String? userid;
  final String? password;

  final ValueChanged<String> onChangedUserid;
  final ValueChanged<String> onChangedPassword;

  const LoginFormWidget({
    Key? key,
    this.userid = '',
    this.password = '',
    required this.onChangedUserid,
    required this.onChangedPassword,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) => SingleChildScrollView(
        child: Padding(
          padding: EdgeInsets.all(16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              buildUserid(),
              SizedBox(height: 8),
              buildPassword(),
              SizedBox(height: 8),
              ElevatedButton(
                  // style: ElevatedButton.styleFrom(
                  //   onPrimary: Colors.white,
                  //   primary: isFormValid ? null : Colors.grey.shade700,
                  // ),
                  onPressed: () {
                    Navigator.push(
                        context, MaterialPageRoute(builder: (_) => Signup()));
                  },
                  child: Text(
                    'Not a user? Create an account',
                    style: TextStyle(color: Colors.white, fontSize: 25),
                  )),

              // style: ElevatedButton.styleFrom(
              //   onPrimary: Colors.white,
              //   primary: isFormValid ? null : Colors.grey.shade700,
              // ),
            ], //Children
          ),
        ),
      );

  Widget buildUserid() => TextFormField(
        maxLines: 1,
        initialValue: userid,
        style: TextStyle(
          color: Colors.white70,
          fontWeight: FontWeight.bold,
          fontSize: 24,
        ),
        decoration: InputDecoration(
          border: InputBorder.none,
          hintText: 'Type your username',
          hintStyle: TextStyle(color: Colors.white70),
        ),
        validator: (userid) =>
            userid != null && userid.isEmpty ? 'Please write a username' : null,
        onChanged: onChangedUserid,
      );

  Widget buildPassword() => TextFormField(
        maxLines: 5,
        initialValue: password,
        style: TextStyle(color: Colors.white60, fontSize: 18),
        decoration: InputDecoration(
          border: InputBorder.none,
          hintText: 'Type your secret password...',
          hintStyle: TextStyle(color: Colors.white60),
        ),
        validator: (password) => password != null && password.isEmpty
            ? 'The password cannot be empty'
            : null,
        onChanged: onChangedPassword,
      );
}
