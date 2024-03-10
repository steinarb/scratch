package no.priv.bang.oldalbum.services.bean;

import java.util.Objects;

public class PathAuth {

    private String path;
    private String auth;

    public String getPath() {
        return path;
    }

    public String getAuth() {
        return auth;
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {

        private String path;
        private String auth;

        public PathAuth build() {
            var bean = new PathAuth();
            bean.path = path;
            bean.auth = auth;
            return bean;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder auth(String auth) {
            this.auth = auth;
            return this;
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(auth, path);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        var other = (PathAuth) obj;
        return Objects.equals(auth, other.auth) && Objects.equals(path, other.path);
    }

    @Override
    public String toString() {
        return "PathAuth [path=" + path + ", auth=" + auth + "]";
    }

}
