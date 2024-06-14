package hr.k33zo.hanabi.networking;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Country implements Externalizable {

    private static final long serialVersionUID = 5L;

    private String name;
    private int code;

    // must have!
    public Country() {
    }

    public Country(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeInt(code);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = in.readUTF();
        code = in.readInt();
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", code=" + code +
                '}';
    }
}
