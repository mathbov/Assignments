import 'package:flutter/material.dart';
import 'package:hoply/db/add_edit_post.dart';
import 'package:hoply/db/tables.dart';
import 'package:intl/intl.dart';
import 'hoply_database.dart';

class PostDetailPage extends StatefulWidget {
  final int postId;
  final String userid;

  const PostDetailPage({
    Key? key,
    required this.postId,
    required this.userid,
  }) : super(key: key);

  @override
  _NoteDetailPageState createState() => _NoteDetailPageState();
}

class _NoteDetailPageState extends State<PostDetailPage> {
  late Posts post;
  bool isLoading = false;
  late List<ReactionsCount> listOfReactions;
  @override
  void initState() {
    super.initState();

    refreshPost();
  }

  Future refreshPost() async {
    setState(() => isLoading = true);

    this.post = await HoplyDatabase.instance.readPost(widget.postId);
    this.listOfReactions =
        await HoplyDatabase.instance.countReactions(widget.postId);

    setState(() => isLoading = false);
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          actions: [deleteButton()],
        ),
        body: isLoading
            ? Center(child: CircularProgressIndicator())
            : Padding(
                padding: EdgeInsets.all(12),
                child: ListView(
                  padding: EdgeInsets.symmetric(vertical: 8),
                  children: [
                    Text(
                      post.title,
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    SizedBox(height: 8),
                    Text(
                      DateFormat.yMMMd().format(post.timestamp),
                      style: TextStyle(color: Colors.white38),
                    ),
                    SizedBox(height: 8),
                    Text(
                      post.text,
                      style: TextStyle(color: Colors.white70, fontSize: 18),
                    ),
                    Padding(
                      padding: EdgeInsets.all(16),
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          ElevatedButton(
                              onPressed: () async {
                                final r = Reactions(
                                    postid: widget.postId,
                                    user: widget.userid,
                                    reactiontype: 0,
                                    timestamp: DateTime.now());
                                await HoplyDatabase.instance.createReaction(r);
                                this.listOfReactions = await HoplyDatabase
                                    .instance
                                    .countReactions(widget.postId);
                              },
                              child: Text(
                                kald(0) + ' Delete',
                                style: TextStyle(
                                    color: Colors.white, fontSize: 25),
                              )),
                          SizedBox(height: 8),
                          ElevatedButton(
                              onPressed: () async {
                                final r = Reactions(
                                    postid: widget.postId,
                                    user: widget.userid,
                                    reactiontype: 1,
                                    timestamp: DateTime.now());
                                await HoplyDatabase.instance.createReaction(r);
                                this.listOfReactions = await HoplyDatabase
                                    .instance
                                    .countReactions(widget.postId);
                              },
                              child: Text(
                                kald(1) + ' Like',
                                style: TextStyle(
                                    color: Colors.white, fontSize: 25),
                              )),
                          SizedBox(height: 8),
                          ElevatedButton(
                              onPressed: () async {
                                final r = Reactions(
                                    postid: widget.postId,
                                    user: widget.userid,
                                    reactiontype: 2,
                                    timestamp: DateTime.now());
                                await HoplyDatabase.instance.createReaction(r);
                                this.listOfReactions = await HoplyDatabase
                                    .instance
                                    .countReactions(widget.postId);
                              },
                              child: Text(
                                kald(2) + ' Hate',
                                style: TextStyle(
                                    color: Colors.white, fontSize: 25),
                              )),
                          SizedBox(height: 8),
                          ElevatedButton(
                              onPressed: () async {
                                final r = Reactions(
                                    postid: widget.postId,
                                    user: widget.userid,
                                    reactiontype: 3,
                                    timestamp: DateTime.now());
                                await HoplyDatabase.instance.createReaction(r);
                                this.listOfReactions = await HoplyDatabase
                                    .instance
                                    .countReactions(widget.postId);
                              },
                              child: Text(
                                kald(3) + ' Couldnt care less',
                                style: TextStyle(
                                    color: Colors.white, fontSize: 25),
                              )),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
      );

  Widget editButton() => IconButton(
      icon: Icon(Icons.edit_outlined),
      onPressed: () async {
        if (isLoading) return;
        await Navigator.of(context).push(MaterialPageRoute(
          builder: (context) => AddEditPostPage(
            post: post,
            userid: post.user,
          ),
        ));

        refreshPost();
      });

  Widget deleteButton() => IconButton(
        icon: Icon(Icons.delete),
        onPressed: () async {
          await HoplyDatabase.instance.deletePost(widget.postId);

          Navigator.of(context).pop();
        },
      );

//Children
  String kald(int type) {
    for (ReactionsCount Rc in listOfReactions) {
      if (Rc.reactiontype == type) {
        return Rc.count.toString();
      }
    }
    return '0';
  }
}
