package pl.expensive.db;

import java.util.UUID;

public class Wallet {
    public final UUID uuid;
    public final String name;

    public Wallet(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Wallet wallet = (Wallet) o;

        if (!uuid.equals(wallet.uuid)) return false;
        if (!name.equals(wallet.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                '}';
    }
}