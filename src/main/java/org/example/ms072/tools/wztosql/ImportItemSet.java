/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.ms072.tools.wztosql;

import  org.example.ms072.client.inventory.MapleInventoryType;
import java.util.Scanner;
import  org.example.ms072.server.MapleItemInformationProvider;
import org.example.ms072.tools.Pair;

/**
 *
 * @author Itzik
 */
public class ImportItemSet {

    public static void main(String[] args) {
        while (true) {
            System.out.println("Set name: ex. Imperial, Timeless, Abyss");
            Scanner input = new Scanner(System.in);
            String name = input.next();
            StringBuilder sb = new StringBuilder();
            for (Pair<Integer, String> item : MapleItemInformationProvider.getInstance().getAllItems2()) {
                if (item.getRight().startsWith(name) && !item.getRight().equals(name) && (MapleItemInformationProvider.getInventoryType(item.getLeft()) == MapleInventoryType.EQUIP || item.getRight().contains("recipe"))) {
                    sb.append(item.getLeft()).append(" - ").append(item.getRight()).append("\r\n");
                }
            }
            System.out.println(sb.toString());
            if (accept(input.next())) {
                main(args);
            }
        }
    }

    public static boolean accept(String toCheck) {
        switch (toCheck.toLowerCase()) {
            case "y":
            case "yes":
            case "true":
                return true;
        }
        return false;
    }
}
