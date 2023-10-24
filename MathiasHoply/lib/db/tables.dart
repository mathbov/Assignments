final String tableUsers = 'users';
final String tablePosts = 'posts';
final String tableReactions = 'reactions';

class UsersFields {
  static final List<String> userValues = [userid, password, name, timestamp];
  static final String userid = '_userid';
  static final String password = 'password';
  static final String name = 'name';
  static final String timestamp = 'timestamp';
}

class PostsFields {
  static final List<String> postValues = [id, title, user, text, timestamp];
  static final String id = '_id';
  static final String title = 'title';
  static final String user = 'user';
  static final String text = 'text';
  static final String timestamp = 'timestamp';
}

class ReactionsFields {
  static final List<String> reactionValues = [
    id,
    user,
    postid,
    reactiontype,
    timestamp
  ];
  static final String id = '_id';
  static final String user = 'user';
  static final String postid = 'postid';
  static final String reactiontype = 'reactiontype';
  static final String timestamp = 'timestamp';
}

class ReactionsCountFields {
  static final List<String> reactionCountValues = [count, reactiontype, postid];
  static final String count = 'count';
  static final String reactiontype = 'reactiontype';
  static final String postid = 'postid';
}

class UsersTableColumns {
  static final String userid = '_userid';
  static final String password = 'password';
  static final String name = 'name';
  static final String timestamp = 'timestamp';
}

class PostsTableColumns {
  static final String id = '_id';
  static final String title = 'title';
  static final String user = 'user';
  static final String text = 'text';
  static final String timestamp = 'timestamp';
}

class ReactionsTableColumns {
  static final String id = '_id';
  static final String user = 'user';
  static final String postid = 'postid';
  static final String reactiontype = 'reactiontype';
  static final String timestamp = 'timestamp';
}

class Users {
  final String userid;
  final String password;
  final String name;
  final DateTime timestamp;

  const Users(
      {required this.userid,
      required this.password,
      required this.name,
      required this.timestamp});
  Users copy({
    String? userid,
    String? password,
    String? name,
    DateTime? timestamp,
  }) =>
      Users(
          userid: userid ?? this.userid,
          password: password ?? this.password,
          name: name ?? this.name,
          timestamp: timestamp ?? this.timestamp);

  static Users userFromJson(Map<String, Object?> json) => Users(
      userid: json[UsersFields.userid] as String,
      password: json[UsersFields.password] as String,
      name: json[UsersFields.name] as String,
      timestamp: DateTime.parse(json[UsersFields.timestamp] as String));

  Map<String, Object?> userToJson() => {
        UsersTableColumns.userid: userid,
        UsersTableColumns.password: password,
        UsersTableColumns.name: name,
        UsersTableColumns.timestamp: timestamp.toIso8601String(),
      };

  factory Users.fromJson(Map<String, dynamic> json) {
    return Users(
        userid: json['id'],
        name: json['name'],
        password: 'xx',
        timestamp: DateTime.parse(json['stamp'] as String));
  }
}

class Posts {
  final int? id;
  final String title;
  final String user;
  final String text;
  final DateTime timestamp;

  const Posts(
      {this.id,
      required this.title,
      required this.user,
      required this.text,
      required this.timestamp});
  Posts copyPost({
    int? id,
    String? title,
    String? user,
    String? text,
    DateTime? timestamp,
  }) =>
      Posts(
          id: id ?? this.id,
          title: title ?? this.title,
          user: user ?? this.user,
          text: text ?? this.text,
          timestamp: timestamp ?? this.timestamp);

  static Posts postFromJson(Map<String, Object?> json) => Posts(
      id: json[PostsFields.id] as int,
      title: json[PostsFields.title] as String,
      user: json[PostsFields.user] as String,
      text: json[PostsFields.text] as String,
      timestamp: DateTime.parse(json[PostsFields.timestamp] as String));

  Map<String, Object?> postToJson() => {
        PostsTableColumns.id: id,
        PostsTableColumns.title: title,
        PostsTableColumns.user: user,
        PostsTableColumns.text: text,
        PostsTableColumns.timestamp: timestamp.toIso8601String(),
      };

  factory Posts.fromJson(Map<String, dynamic> json) {
    return Posts(
        id: json['id'],
        user: json['user_id'],
        title: 'Dum titel',
        text: json['content'],
        timestamp: DateTime.parse(json['stamp'] as String));
  }
}

class Reactions {
  final int? id;
  final String user;
  final int postid;
  final int reactiontype;
  final DateTime timestamp;

  const Reactions(
      {this.id,
      required this.user,
      required this.postid,
      required this.reactiontype,
      required this.timestamp});

  Reactions copyReaction({
    int? id,
    String? user,
    int? postid,
    int? reactiontype,
    DateTime? timestamp,
  }) =>
      Reactions(
          id: id ?? this.id,
          user: user ?? this.user,
          postid: postid ?? this.postid,
          reactiontype: reactiontype ?? this.reactiontype,
          timestamp: timestamp ?? this.timestamp);

  static Reactions reactionFromJson(Map<String, Object?> json) => Reactions(
      id: json[ReactionsFields.id] as int,
      user: json[ReactionsFields.user] as String,
      postid: json[ReactionsFields.postid] as int,
      reactiontype: json[ReactionsFields.reactiontype] as int,
      timestamp: DateTime.parse(json[ReactionsFields.timestamp] as String));

  Map<String, Object?> reactionToJson() => {
        ReactionsTableColumns.id: id,
        ReactionsTableColumns.user: user,
        ReactionsTableColumns.postid: postid,
        ReactionsTableColumns.reactiontype: reactiontype,
        ReactionsTableColumns.timestamp: timestamp.toIso8601String(),
      };

  factory Reactions.fromJson(Map<String, dynamic> json) {
    return Reactions(
        id: 0,
        user: json['user_id'],
        postid: json['post_id'],
        reactiontype: json['type'],
        timestamp: DateTime.parse(json['stamp'] as String));
  }
}

class ReactionsCount {
  final int? count;
  final int? reactiontype;
  final int? postid;

  const ReactionsCount({
    this.count,
    this.reactiontype,
    this.postid,
  });

  static ReactionsCount reactionsCountFromJson(Map<String, Object?> json) =>
      ReactionsCount(
          count: json[ReactionsCountFields.count] as int?,
          reactiontype: json[ReactionsCountFields.reactiontype] as int?,
          postid: json[ReactionsCountFields.postid] as int?);
}
