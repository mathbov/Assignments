import 'package:flutter/material.dart';
import 'package:path/path.dart';
import 'package:hoply/db/tables.dart';
import 'package:sqflite/sqflite.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class HoplyDatabase {
  static final HoplyDatabase instance = HoplyDatabase._init();
  static Database? _database;

  HoplyDatabase._init();

  Future<Database> get database async {
    if (_database != null) return _database!;

    _database = await _initDB('hoply5.db');
    return _database!;
  }

  Future<Database> _initDB(String filePath) async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, filePath);

    return await openDatabase(path, version: 2, onCreate: _createDB);
  }

  Future _createDB(Database db, int version) async {
    final useridType = 'TEXT PRIMARY KEY';
    final passwordType = 'TEXT NOT NULL';
    final nameType = 'TEXT NOT NULL';
    final timestampType = 'TIMESTAMP';
    final idType = 'INTEGER PRIMARY KEY AUTOINCREMENT';
    final titleType = 'TEXT NOT NULL';
    final userType = 'TEXT NOT NULL';
    final textType = 'TEXT NOT NULL';
    final postidType = 'INTEGER NOT NULL';
    final reactionType = 'INTEGER NOT NULL';

    await db.execute('''
      CREATE TABLE $tableUsers(
        ${UsersTableColumns.userid} $useridType,
        ${UsersTableColumns.password} $passwordType,
        ${UsersTableColumns.name} $nameType,
        ${UsersTableColumns.timestamp} $timestampType
    )
    ''');
    await db.execute('''
        CREATE TABLE $tablePosts(
        ${PostsTableColumns.id} $idType,
        ${PostsTableColumns.title} $titleType,
        ${PostsTableColumns.user} $userType,
        ${PostsTableColumns.text} $textType,
        ${PostsTableColumns.timestamp} $timestampType
        )
  ''');
    await db.execute('''
         CREATE TABLE $tableReactions(
        ${ReactionsTableColumns.id} $idType,
        ${ReactionsTableColumns.user} $userType,
        ${ReactionsTableColumns.postid} $postidType,
        ${ReactionsTableColumns.reactiontype} $reactionType,
        ${ReactionsTableColumns.timestamp} $timestampType
    )
     ''');
  }

  Future<Users> createUser(Users user) async {
    final db = await instance.database;
    await syncUsers();
    final userid = await db.insert(tableUsers, user.userToJson());
    await syncToUserWeb(user);
    return user.copy(userid: user.userid);
  }

  Future<bool> syncUsers() async {
    final db = await instance.database;
    final orderBy = '${UsersFields.timestamp} DESC';
    final result = await db
        .rawQuery('SELECT * FROM $tableUsers ORDER BY $orderBy LIMIT 1 ');
    final u = Users.userFromJson(result.first);
    final String lastTime = u.timestamp.toIso8601String();
    final response = await http.get(Uri.parse(
        'https://caracal.imada.sdu.dk/app2022/users?stamp=gt.$lastTime%2B02:00'));

    if (response.statusCode == 200) {
      Iterable listJson = json.decode(response.body);
      try {
        List<Users> listUsers =
            List<Users>.from(listJson.map((model) => Users.fromJson(model)));
        for (var u in listUsers) {
          await db.insert(tableUsers, u.userToJson());
        }
        return true;
      } catch (e) {
        return false;
      }
    } else {
      throw Exception('Failed to load users');
    }
  }

  Future<bool> syncToUserWeb(Users user) async {
    Map<String, String> requestHeaders = {
      'Content-Type': 'application/json',
      'Authorization':
          'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYXBwMjAyMiJ9.iEPYaqBPWoAxc7iyi507U3sexbkLHRKABQgYNDG4Awk',
    };
    try {
      final response = await http.post(
          Uri.parse('https://caracal.imada.sdu.dk/app2022/users'),
          headers: requestHeaders,
          body: jsonEncode(<String, String>{
            'id': user.userid,
            'name': user.name,
          }));
      if (response.statusCode == 201) {
        return true;
      } else {
        throw Exception('Failed to create user');
      }
    } catch (e) {
      print(e);
      throw e;
    }
  }

  Future<Users> readUser(String userid) async {
    final db = await instance.database;

    final maps = await db.query(tableUsers,
        columns: UsersFields.userValues,
        where: '${UsersFields.userid} = ?',
        whereArgs: [userid]);
    if (maps.isNotEmpty) {
      return Users.userFromJson(maps.first);
    } else {
      throw Exception('Userid $userid not found');
    }
  }

  Future<List<Users>> readAllUsers() async {
    final db = await instance.database;
    final orderBy = '${UsersFields.userid} ASC';
    //  final result = await db.query(tableUsers, orderBy: orderBy);
    final result =
        await db.rawQuery('SELECT * FROM $tableUsers ORDER BY $orderBy');

    return result.map((json) => Users.userFromJson(json)).toList();
  }

  Future<int> delete(String userid) async {
    final db = await instance.database;
    return await db.delete(tableUsers,
        where: '${UsersFields.userid} = ?', whereArgs: [userid]);
  }

  Future<Posts> createPost(Posts post) async {
    final db = await instance.database;
    await syncPosts();
    final id = await db.insert(tablePosts, post.postToJson());
    await syncToPostWeb(post, id);
    return post.copyPost(id: post.id);
  }

  Future<bool> syncPosts() async {
    final db = await instance.database;
    final orderBy = '${PostsFields.timestamp} DESC';
    final result = await db
        .rawQuery('SELECT * FROM $tablePosts ORDER BY $orderBy LIMIT 1 ');
    final p = Posts.postFromJson(result.first);
    final String lastTime = p.timestamp.toIso8601String();
    final response = await http.get(Uri.parse(
        'https://caracal.imada.sdu.dk/app2022/posts?stamp=gt.$lastTime%2B02:00'));

    if (response.statusCode == 200) {
      Iterable listJson = json.decode(response.body);
      try {
        List<Posts> listPosts =
            List<Posts>.from(listJson.map((model) => Posts.fromJson(model)));
        for (var p in listPosts) {
          await db.insert(tablePosts, p.postToJson());
        }
        return true;
      } catch (e) {
        return false;
      }
    } else {
      throw Exception('Failed to load posts');
    }
  }

  Future<bool> syncToPostWeb(Posts posten, int id) async {
    Map<String, String> requestHeaders = {
      'Content-Type': 'application/json',
      'Authorization':
          'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYXBwMjAyMiJ9.iEPYaqBPWoAxc7iyi507U3sexbkLHRKABQgYNDG4Awk',
    };
    try {
      final response = await http.post(
          Uri.parse('https://caracal.imada.sdu.dk/app2022/posts'),
          headers: requestHeaders,
          body: jsonEncode(<String, String>{
            'id': id.toString(),
            'user_id': posten.user,
            'content': posten.text,
          }));
      if (response.statusCode == 201) {
        return true;
      } else {
        throw Exception('Failed to create post');
      }
    } catch (e) {
      print(e);
      throw e;
    }
  }

  Future<Posts> readPost(int id) async {
    final db = await instance.database;

    final maps = await db.query(tablePosts,
        columns: PostsFields.postValues,
        where: '${PostsFields.id} = ?',
        whereArgs: [id]);
    if (maps.isNotEmpty) {
      return Posts.postFromJson(maps.first);
    } else {
      throw Exception('postid $id not found');
    }
  }

  Future<List<Posts>> readAllPosts() async {
    final db = await instance.database;
    final orderBy = '${PostsFields.id} ASC';
    // final result = await db.query(tablePosts, orderBy: orderBy);
    final result =
        await db.rawQuery('SELECT * FROM $tablePosts ORDER BY $orderBy');
    return result.map((json) => Posts.postFromJson(json)).toList();
  }

  Future<int> updatePost(Posts post) async {
    final db = await instance.database;

    return db.update(
      tablePosts,
      post.postToJson(),
      where: '${PostsFields.id} = ?',
      whereArgs: [post.id],
    );
  }

  Future<int> deletePost(int id) async {
    final db = await instance.database;
    return await db
        .delete(tablePosts, where: '${PostsFields.id} = ?', whereArgs: [id]);
  }

  Future<Reactions> createReaction(Reactions reaction) async {
    final db = await instance.database;
    await syncReactions();
    await db.delete(tableReactions,
        where: '${ReactionsFields.postid} = ? AND ${ReactionsFields.user} = ?',
        whereArgs: [reaction.postid, reaction.user]);
    final id = await db.insert(tableReactions, reaction.reactionToJson());
    await syncToReactionWeb(reaction);
    return reaction.copyReaction(id: reaction.id);
  }

  Future<bool> syncReactions() async {
    final db = await instance.database;
    final orderBy = '${ReactionsFields.timestamp} DESC';
    final result = await db
        .rawQuery('SELECT * FROM $tableReactions ORDER BY $orderBy LIMIT 1 ');
    final r = Reactions.reactionFromJson(result.first);
    final String lastTime = r.timestamp.toIso8601String();
    final response = await http.get(Uri.parse(
        'https://caracal.imada.sdu.dk/app2022/reactions?stamp=gt.$lastTime%2B02:00'));

    if (response.statusCode == 200) {
      Iterable listJson = json.decode(response.body);
      try {
        List<Reactions> listReactions = List<Reactions>.from(
            listJson.map((model) => Reactions.fromJson(model)));
        for (var p in listReactions) {
          await db.insert(tableReactions, p.reactionToJson());
        }
        return true;
      } catch (e) {
        return false;
      }
    } else {
      throw Exception('Failed to load reactions');
    }
  }

  Future<bool> syncToReactionWeb(Reactions reaction) async {
    Map<String, String> requestHeaders = {
      'Content-Type': 'application/json',
      'Authorization':
          'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYXBwMjAyMiJ9.iEPYaqBPWoAxc7iyi507U3sexbkLHRKABQgYNDG4Awk',
    };
    final user_id = reaction.user;
    final post_id = reaction.postid.toString();
    try {
      final deleteResponse = await http.delete(
          Uri.parse(
              'https://caracal.imada.sdu.dk/app2022/reactions?user_id=eq.$user_id&post_id=eq.$post_id'),
          headers: requestHeaders);
      if (deleteResponse.statusCode == 204) {}

      final response = await http.post(
          Uri.parse('https://caracal.imada.sdu.dk/app2022/reactions'),
          headers: requestHeaders,
          body: jsonEncode(<String, String>{
            'user_id': reaction.user,
            'post_id': reaction.postid.toString(),
            'type': reaction.reactiontype.toString(),
          }));
      if (response.statusCode == 201) {
        return true;
      } else {
        throw Exception('Failed to create reaction');
      }
    } catch (e) {
      print(e);
      throw e;
    }
  }

  Future<Reactions> readReaction(int id) async {
    final db = await instance.database;

    final maps = await db.query(tableReactions,
        columns: ReactionsFields.reactionValues,
        where: '${ReactionsFields.id} = ?',
        whereArgs: [id]);
    if (maps.isNotEmpty) {
      return Reactions.reactionFromJson(maps.first);
    } else {
      throw Exception('postid $id not found');
    }
  }

  Future<List<ReactionsCount>> countReactions(int postid) async {
    final db = await instance.database;
    final orderBy = '${ReactionsFields.reactiontype} ASC';
    // final result = await db.query(tablePosts, orderBy: orderBy);
    final result = await db.rawQuery(
        'SELECT COUNT(*) as count, reactiontype  FROM $tableReactions ' +
            'WHERE postid = $postid GROUP BY reactiontype ORDER BY $orderBy');
    return result
        .map((json) => ReactionsCount.reactionsCountFromJson(json))
        .toList();
  }

  Future<int> deleteReaction(String userid, int postid) async {
    final db = await instance.database;
    return await db.delete(tableReactions,
        where: '${ReactionsFields.postid} = ? AND ${ReactionsFields.user} = ?',
        whereArgs: [postid, userid]);
  }

  Future close() async {
    final db = await instance.database;
    db.close();
  }
}
