import 'package:flutter/material.dart';
import 'package:hoply/db/post_form_widget.dart';
import 'package:hoply/db/tables.dart';

import 'hoply_database.dart';

class AddEditPostPage extends StatefulWidget {
  final Posts? post;
  final String userid;

  const AddEditPostPage({
    Key? key,
    this.post,
    required this.userid,
  }) : super(key: key);
  @override
  _AddEditPostPageState createState() => _AddEditPostPageState();
}

class _AddEditPostPageState extends State<AddEditPostPage> {
  final _formKey = GlobalKey<FormState>();
  late String title;
  late String user;
  late String text;

  @override
  void initState() {
    super.initState();

    title = widget.post?.title ?? '';
    user = widget.post?.user ?? '';
    text = widget.post?.text ?? '';
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          actions: [buildButton()],
        ),
        body: Form(
          key: _formKey,
          child: PostFormWidget(
            //       isImportant: isImportant,
            //      number: number,
            title: title,
            text: text,
            //      onChangedImportant: (isImportant) =>
            //          setState(() => this.isImportant = isImportant),
            //      onChangedNumber: (number) => setState(() => this.number = number),
            onChangedTitle: (title) => setState(() => this.title = title),
            onChangedText: (text) => setState(() => this.text = text),
          ),
        ),
      );

  Widget buildButton() {
    final isFormValid = title.isNotEmpty && text.isNotEmpty;

    return Padding(
      padding: EdgeInsets.symmetric(vertical: 8, horizontal: 12),
      child: ElevatedButton(
        style: ElevatedButton.styleFrom(
          onPrimary: Colors.white,
          primary: isFormValid ? null : Colors.grey.shade700,
        ),
        onPressed: addOrUpdatePost,
        child: Text('Save'),
      ),
    );
  }

  void addOrUpdatePost() async {
    final isValid = _formKey.currentState!.validate();

    if (isValid) {
      final isUpdating = widget.post != null;

      if (isUpdating) {
        await updatePost();
      } else {
        await addPost();
      }

      Navigator.of(context).pop();
    }
  }

  Future updatePost() async {
    final post = widget.post!.copyPost(
      //     isImportant: isImportant,
      //    number: number,
      title: title,
      text: text,
    );

    await HoplyDatabase.instance.updatePost(post);
  }

  Future addPost() async {
    final post = Posts(
      title: title,
      user: widget.userid,
      text: text,
      timestamp: DateTime.now(),
    );

    await HoplyDatabase.instance.createPost(post);
  }
}
