
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;


public class test {

    public static void main(String[] args) {
        for (LootTables table : LootTables.values()) {
            System.out.println(table);
        }
    }
    
}
