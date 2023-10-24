import 'package:flutter/material.dart';

class SignupFormWidget extends StatelessWidget {
  final String? userid;
  final String? name;
  final String? password;

  final ValueChanged<String> onChangedUserid;
  final ValueChanged<String> onChangedName;
  final ValueChanged<String> onChangedPassword;

  const SignupFormWidget({
    Key? key,
    this.userid = '',
    this.name = '',
    this.password = '',
    required this.onChangedUserid,
    required this.onChangedName,
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
              buildName(),
              SizedBox(height: 8),
              buildPassword(),
              SizedBox(height: 8)
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
          hintText: 'Type a cool username',
          hintStyle: TextStyle(color: Colors.white70),
        ),
        validator: (userid) =>
            userid != null && userid.isEmpty ? 'Please write a username' : null,
        onChanged: onChangedUserid,
      );

  Widget buildName() => TextFormField(
        maxLines: 5,
        initialValue: name,
        style: TextStyle(color: Colors.white60, fontSize: 18),
        decoration: InputDecoration(
          border: InputBorder.none,
          hintText: 'Type your name...',
          hintStyle: TextStyle(color: Colors.white60),
        ),
        validator: (name) => name != null && name.isEmpty
            ? 'The description cannot be empty'
            : null,
        onChanged: onChangedName,
      );
  Widget buildPassword() => TextFormField(
        maxLines: 5,
        initialValue: password,
        style: TextStyle(color: Colors.white60, fontSize: 18),
        decoration: InputDecoration(
          border: InputBorder.none,
          hintText: 'Important secret password...',
          hintStyle: TextStyle(color: Colors.white60),
        ),
        validator: (password) => password != null && password.isEmpty
            ? 'The password cannot be empty'
            : null,
        onChanged: onChangedPassword,
      );
}
