package id.go.kominfo.fitri.aplikasisqlite1.dao;

import java.util.List;
import id.go.kominfo.fitri.aplikasisqlite1.model.Friend;

public interface FriendDao {
    void insert(Friend friend);
    void update(Friend friend);
    void delete(int id);
    Friend getFriendById(int id);
    List<Friend> getAllFriends();
}
