package org.thanhmagics.dgcore.gui;

import org.bukkit.entity.Player;
import org.thanhmagics.DGCore;
import org.thanhmagics.utils.XMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SignGUI {

    public List<String> lines = new ArrayList<>(Arrays.asList("", "", "", ""));

    public SignGUI setLine(int i, String content) {
        if (i > 3)
            return this;
        if (content != null) {
            this.lines.set(i, content);
            return this;
        }
        this.lines.set(i, "");
        return this;
    }

    public abstract void onClose(Player player, List<String> lines);


    public SignGUI open(Player player) {
        DGCore.getInstance().nmsManager.openSG(player, this,DGCore.getInstance(), XMaterial.OAK_SIGN.parseMaterial());
        return this;
    }
}
