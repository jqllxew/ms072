package tools.wztosql.cashShopwz;

import client.inventory.MapleInventoryType;
import database.DatabaseConnection;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.CashItemFactory;
import server.CashItemInfo.CashModInfo;
import server.MapleItemInformationProvider;
import tools.FileoutputUtil;

/**
 *
 * @author Flower
 */
public class wz个人商店 {

    private static final MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File((System.getProperty("wzpath") != null ? System.getProperty("wzpath") : "") + "wz/Etc.wz"));

    public static final CashModInfo getModInfo(int sn) {
        CashModInfo ret = null;
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_502_cashshop_其他_个人商店 WHERE serial = ?")) {
            ps.setInt(1, sn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));

                }
            }

        } catch (Exception ex) {
            FileoutputUtil.printError("CashShopDumper.txt", ex);
        }

        return ret;
    }

    public static void main(String[] args) {
        CashModInfo m = getModInfo(20000393);
        CashItemFactory.getInstance().initialize_502();
        Collection<CashModInfo> list = CashItemFactory.getInstance().getAllModInfo();

        final List<Integer> itemids = new ArrayList<Integer>();
        List<Integer> qq = new ArrayList<Integer>();

        //所有的道具
        Map<Integer, Map<String, Integer>> itemConmes = new HashMap<>();
        for (MapleData field : data.getData("Commodity.img").getChildren()) {
            Map<String, Integer> itemConme = new HashMap<>();
            int itemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            int sn = MapleDataTool.getIntConvert("SN", field, 0);
            int count = MapleDataTool.getIntConvert("Count", field, 0);
            int price = MapleDataTool.getIntConvert("Price", field, 0);
            int priority = MapleDataTool.getIntConvert("Priority", field, 0);
            int period = MapleDataTool.getIntConvert("Period", field, 0);
            int gender = MapleDataTool.getIntConvert("Gender", field, -1);
            int meso = MapleDataTool.getIntConvert("Meso", field, 0);
            if (price == 0) {
                continue;
            }
            if (sn / 10000000 == 2 || sn / 10000000 == 3 || sn / 10000000 == 5 || sn / 10000000 == 6 || sn / 10000000 == 7) {
                if (itemConmes.containsKey(itemId)) {
                    if (itemConmes.get(itemId).get("price") > price) {
                        continue;
                    }
                }
            }
            itemConme.put("itemId", itemId);
            itemConme.put("sn", sn);
            itemConme.put("count", count);
            itemConme.put("price", price);
            itemConme.put("priority", priority);
            itemConme.put("period", period);
            itemConme.put("gender", gender);
            itemConme.put("meso", meso);
            itemConmes.put(itemId, itemConme);
        }

        Map<Integer, List<String>> dics = new HashMap<>();
        for (Map<String, Integer> field : itemConmes.values()) {
            try {
                final int itemId = field.get("itemId");
                final int sn = field.get("sn");
                final int count = field.get("count");
                final int price = field.get("price");
                final int priority = field.get("priority");
                final int period = field.get("period");
                final int gender = field.get("gender");
                final int meso = field.get("meso");
                //if(qq.contains(itemId))
                //    continue;
                if (itemId == 0) {
                    continue;
                }
                if (sn > 80000000) {
                    continue;
                }
                if (price == 0) {
                    continue;
                }
                if (sn / 100000 != 502) {
                    continue;
                }
                int cat = itemId / 10000;
                if (dics.get(cat) == null) {
                    dics.put(cat, new ArrayList());
                }
                boolean check = false;
                if (meso > 0) {
                    check = true;
                }
                if (MapleItemInformationProvider.getInstance().getInventoryTypeCS(itemId) == MapleInventoryType.EQUIP) {
                    if (!MapleItemInformationProvider.getInstance().isCashItem(itemId)) {
                        check = true;
                    }
                }
                if (MapleItemInformationProvider.getInstance().getInventoryTypeCS(itemId) == MapleInventoryType.EQUIP) {
                    if (period > 0) {
                        check = true;
                    }
                }

                if (check) {
                    System.out.println(MapleItemInformationProvider.getInstance().getName(itemId));
                    continue;
                }

                try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO wz_502_cashshop_其他_个人商店 (serial, showup,itemid,priority,period,gender,count,meso,discount_price,mark, unk_1, unk_2, unk_3) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, sn);
                    ps.setInt(2, 1);
                    ps.setInt(3, itemId);
                    ps.setInt(4, 0);
                    ps.setInt(5, period);
                    ps.setInt(6, gender);
                    ps.setInt(7, count >= 1 ? count : 0);
                    ps.setInt(8, meso);
                    if ((1000000 <= itemId || itemId <= 1003091) && sn >= 20000000) {
                        ps.setInt(9, price);
                    } else {
                        ps.setInt(9, 0);
                    }
                    qq.add(itemId);
                    ps.setInt(10, 0);
                    ps.setInt(11, 0);
                    ps.setInt(12, 0);
                    ps.setInt(13, 0);
                    //ps.setString(14, MapleItemInformationProvider.getInstance().getName(itemId));

                    //String sql = ps.toString().split(":")[1].trim() + ";";
                    ps.executeUpdate();
                    ps.toString();
                }

            } catch (SQLException ex) {
                FileoutputUtil.printError("CashShopDumper.txt", ex);
            }

        }

        /*for (Integer key : dics.keySet()) {

         File fout = new File("cashshopItems/" + key.toString() + ".sql");
         List<String> l = dics.get(key);
         FileOutputStream fos = null;
         try {
         if (!fout.exists()) {
         fout.createNewFile();
         }
         fos = new FileOutputStream(fout);
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
         for (int i = 0; i < l.size(); i++) {
         bw.write(l.get(i));
         bw.newLine();
         }

         bw.close();

         } catch (FileNotFoundException ex) {
         FileoutputUtil.printError("CashShopDumper.txt", ex);
         } catch (IOException ex) {
         FileoutputUtil.printError("CashShopDumper.txt", ex);
         } finally {
         try {
         if (fos != null) {
         fos.close();
         }
         } catch (IOException ex) {
         FileoutputUtil.printError("CashShopDumper.txt", ex);
         }
         }

         }*/
    }
}
