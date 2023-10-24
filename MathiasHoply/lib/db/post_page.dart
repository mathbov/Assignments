import 'package:flutter/material.dart';
import 'package:flutter_staggered_grid_view/flutter_staggered_grid_view.dart';
import 'package:hoply/db/post_card_widget.dart';
import 'package:hoply/db/tables.dart';

import 'add_edit_post.dart';
import 'post_detail_page.dart';
import 'hoply_database.dart';

class PostPage extends StatefulWidget {
  const PostPage({Key? key, required this.userid}) : super(key: key);
  final String userid;
  @override
  _PostPageState createState() => _PostPageState();
}

class _PostPageState extends State<PostPage> {
  late List<Posts> posts;

  bool isLoading = false;

  @override
  void initState() {
    super.initState();

    refreshPosts();
  }

  @override
  void dispose() {
    //HoplyDatabase.instance.close();

    super.dispose();
  }

  Future refreshPosts() async {
    setState(() => isLoading = true);

    this.posts = await HoplyDatabase.instance.readAllPosts();

    setState(() => isLoading = false);
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          title: Text(
            'Posts',
            style: TextStyle(fontSize: 24),
          ),
          actions: [Icon(Icons.search), SizedBox(width: 12)],
        ),
        body: Center(
          child: isLoading
              ? CircularProgressIndicator()
              : posts.isEmpty
                  ? Text(
                      'No Posts',
                      style: TextStyle(color: Colors.white, fontSize: 24),
                    )
                  : buildNotes(),
        ),
        floatingActionButton: FloatingActionButton(
          backgroundColor: Colors.black,
          child: Icon(Icons.add),
          onPressed: () async {
            await Navigator.of(context).push(
              MaterialPageRoute(
                  builder: (context) => AddEditPostPage(userid: widget.userid)),
            );

            refreshPosts();
          },
        ),
      );

  Widget buildNotes() => StaggeredGridView.countBuilder(
        padding: EdgeInsets.all(8),
        itemCount: posts.length,
        staggeredTileBuilder: (index) => StaggeredTile.fit(2),
        crossAxisCount: 4,
        mainAxisSpacing: 4,
        crossAxisSpacing: 4,
        itemBuilder: (context, index) {
          final post = posts[index];

          return GestureDetector(
            onTap: () async {
              await Navigator.of(context).push(MaterialPageRoute(
                builder: (context) =>
                    PostDetailPage(postId: post.id!, userid: widget.userid),
              ));

              refreshPosts();
            },
            child: PostCardWidget(post: post, index: index),
          );
        },
      );
}
