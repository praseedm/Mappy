package praseed.p6c.mappy;

/**
 * Created by praseedm on 10/24/2016.
 */

public class UserObj {
    private String name;
    private String photoUri;
    private String id;
    private String email;

    public UserObj() {
    }

    public UserObj(String name, String photoUri, String id, String email) {
        this.name = name;
        this.photoUri = photoUri;
        this.id = id;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
