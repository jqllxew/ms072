package server;

import database.DatabaseConnection;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.CashItemInfo.CashModInfo;
import server.CashItemInfo.CashPackageInfo;
import tools.FileoutputUtil;

public class CashItemFactory {

    private final static CashItemFactory instance = new CashItemFactory();
    private boolean initialized = false;
    //private final static int[] bestItems = new int[]{50400041, 50400016, 50400017, 50400009, 50400032};
    private final static int[] bestItems = new int[]{99999991, 99999992, 99999993, 99999994, 99999995};
    private final Map<Integer, CashItemInfo> itemStats = new HashMap<>();
    private final Map<Integer, List<Integer>> itemPackage = new HashMap<>();
    private final Map<Integer, CashPackageInfo> itemPks = new HashMap<>();
    private final Map<Integer, CashModInfo> itemMods = new HashMap<>();
    private final Map<Integer, List<Integer>> openBox = new HashMap<>();
    private final MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File((System.getProperty("wzpath") != null ? System.getProperty("wzpath") : "") + "wz/Etc.wz"));
    private final List<CashCategory> categories = new LinkedList<>();
    private final Map<Integer, CashItem> menuItems = new HashMap<>();
    private final Map<Integer, CashItem> categoryItems = new HashMap<>();

    public static CashItemFactory getInstance() {
        return instance;
    }

    public void clearCashShop() {
        itemStats.clear();
        itemPackage.clear();
        itemMods.clear();
        itemPks.clear();
        categories.clear();
        menuItems.clear();
        categoryItems.clear();
        initialized = false;
    }

    public final CashItemInfo getPackageItem(int sn) {
        final CashItemInfo stats = itemStats.get(sn);
        final CashPackageInfo cc = getitemPksInfo(sn);
        if (cc != null) {
            return cc.toCItem(stats); //null doesnt matter
        }
        return stats;
    }

    public final CashItemInfo getSimpleItem(int sn) {
        return itemStats.get(sn);
    }

    public final CashPackageInfo getitemPksInfo(int sn) {
        return itemPks.get(sn);
    }

    public final CashItemInfo getItem(int sn) {
        final CashItemInfo stats = itemStats.get(sn);
        final CashModInfo z = getModInfo(sn);
        if (z != null && z.showUp) {
            return z.toCItem(stats); //null doesnt matter
        }
        if (stats == null || !stats.onSale()) {
            return null;
        }
        return stats;
    }

    public final CashItem getMenuItem(int sn) {
        for (CashItem ci : getMenuItems()) {
            if (ci.getSN() == sn) {
                return ci;
            }
        }
        return null;
    }

    public final CashItem getAllItem(int sn) {
        for (CashItem ci : getAllItems()) {
            if (ci.getSN() == sn) {
                return ci;
            }
        }
        return null;
    }

    public final List<Integer> getPackageItems(int itemId) {
        return itemPackage.get(itemId);
    }

    public final CashModInfo getModInfo(int sn) {
        return itemMods.get(sn);
    }

    public final Collection<CashModInfo> getAllModInfo() {
        if (!initialized) {
            initialize();
        }
        return itemMods.values();
    }

    public final Map<Integer, List<Integer>> getRandomItemInfo() {
        return openBox;
    }

    public final int[] getBestItems() {
        return bestItems;
    }

    public final List<CashCategory> getCategories() {
        return categories;
    }

    public final List<CashItem> getMenuItems(int type) {
        List<CashItem> items = new LinkedList();
        for (CashItem ci : menuItems.values()) {
            if (ci.getSubCategory() / 10000 == type) {
                items.add(ci);
            }
        }
        return items;
    }

    public final List<CashItem> getMenuItems() {
        List<CashItem> items = new LinkedList();
        for (CashItem ci : menuItems.values()) {
            items.add(ci);
        }
        return items;
    }

    public final List<CashItem> getAllItems(int type) {
        List<CashItem> items = new LinkedList();
        for (CashItem ci : categoryItems.values()) {
            if (ci.getSubCategory() / 10000 == type) {
                items.add(ci);
            }
        }
        return items;
    }

    public final List<CashItem> getAllItems() {
        List<CashItem> items = new LinkedList();
        for (CashItem ci : categoryItems.values()) {
            items.add(ci);
        }
        return items;
    }

    public final List<CashItem> getCategoryItems(int subcategory) {
        List<CashItem> items = new LinkedList();
        for (CashItem ci : categoryItems.values()) {
            if (ci.getSubCategory() == subcategory) {
                items.add(ci);
            }
        }
        return items;
    }

    public final int getItemSN(int itemid) {
        for (Entry<Integer, CashItemInfo> ci : itemStats.entrySet()) {
            if (ci.getValue().getId() == itemid) {
                return ci.getValue().getSN();
            }
        }
        return 0;
    }

    public void initialize(){
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int ItemId = rs.getInt("itemid");
                int Count = rs.getInt("count");
                int Price = rs.getInt("discount_price");
                int SN = rs.getInt("serial");
                int Period = rs.getInt("period");
                int Gender = rs.getInt("gender");
                boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);

                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }

        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }

        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show =  MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn,
                        rs.getInt("discount_price"),
                        rs.getInt("mark"),
                        rs.getInt("showup") > 0,
                        rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);
                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(ret.sn);
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
            initialized = true;
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
    }

    public void initialize_100() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_100_cashshop_首页_推荐商品");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_100_cashshop_首页_推荐商品");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_101() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_101_cashshop_首页_达人专区");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_101_cashshop_首页_达人专区");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_102() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_102_cashshop_首页_活动");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_102_cashshop_首页_活动");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_200() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_200_cashshop_装备_帽子");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_200_cashshop_装备_帽子");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_201() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_201_cashshop_装备_脸饰");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_201_cashshop_装备_脸饰");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_202() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_202_cashshop_装备_眼饰");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_202_cashshop_装备_眼饰");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_203() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_203_cashshop_装备_长袍");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_203_cashshop_装备_长袍");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_204() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_204_cashshop_装备_上衣");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_204_cashshop_装备_上衣");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_205() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_205_cashshop_装备_裤裙");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_205_cashshop_装备_裤裙");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_206() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_206_cashshop_装备_鞋子");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_206_cashshop_装备_鞋子");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_207() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_207_cashshop_装备_手套");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_207_cashshop_装备_手套");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_208() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_208_cashshop_装备_武器");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_208_cashshop_装备_武器");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_209() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_209_cashshop_装备_戒指");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_209_cashshop_装备_戒指");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_210() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_210_cashshop_装备_镖");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_210_cashshop_装备_镖");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_211() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_211_cashshop_装备_披风");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_211_cashshop_装备_披风");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_212() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_212_cashshop_装备_骑宠");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_212_cashshop_装备_骑宠");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_300() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_300_cashshop_消耗_喜庆商品");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_300_cashshop_消耗_喜庆商品");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_301() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_301_cashshop_消耗_通讯物品");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_301_cashshop_消耗_通讯物品");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_302() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_302_cashshop_消耗_卷轴");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_302_cashshop_消耗_卷轴");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_500() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_500_cashshop_其他_会员卡");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_500_cashshop_其他_会员卡");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_501() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_501_cashshop_其他_表情");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_501_cashshop_其他_表情");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_502() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_502_cashshop_其他_个人商店");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_502_cashshop_其他_个人商店");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_503() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_503_cashshop_其他_纪念日");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_503_cashshop_其他_纪念日");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_504() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_504_cashshop_其他_游戏");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_504_cashshop_其他_游戏");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_505() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_505_cashshop_其他_效果");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_505_cashshop_其他_效果");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_600() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_600_cashshop_宠物_宠物");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_600_cashshop_宠物_宠物");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_601() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_601_cashshop_宠物_其他");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_601_cashshop_宠物_其他");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_602() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_602_cashshop_宠物_宠物服饰");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_602_cashshop_宠物_宠物服饰");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

    public void initialize_700() {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_700_cashshop_礼包");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer ItemId = rs.getInt("itemid");
                Integer Count = rs.getInt("count");
                Integer Price = rs.getInt("discount_price");
                Integer SN = rs.getInt("serial");
                Integer Period = rs.getInt("Period");
                Integer Gender = rs.getInt("gender");
                Boolean showUp = rs.getInt("showup") > 0;
                final CashItemInfo stats = new CashItemInfo(ItemId, Count, Price, SN, Period, Gender, showUp, 0);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("CashItemInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int Price = MapleDataTool.getIntConvert("Price", field, 0);
            final boolean show = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0;
            final int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int Period = MapleDataTool.getIntConvert("Period", field, 0);
            final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final int Count = MapleDataTool.getIntConvert("Count", field, 1);
            CashPackageInfo ret = new CashPackageInfo(SN, Price, 0, show, ItemId, 0, false, Period, Gender, Count, 0, 0, 0, 0, 0);
            itemPks.put(SN, ret);

            if (ret.showUp) {
                final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                if (cc != null) {
                    ret.toCItem(cc); //init
                }
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_700_cashshop_礼包");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                    if (cc != null) {
                        ret.toCItem(cc); //init
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CashModInfo" + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
        }
        initialized = true;
    }

}
