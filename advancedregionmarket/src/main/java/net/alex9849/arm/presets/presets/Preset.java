package net.alex9849.arm.presets.presets;

import net.alex9849.arm.Messages;
import net.alex9849.arm.entitylimit.EntityLimitGroup;
import net.alex9849.arm.flaggroups.FlagGroup;
import net.alex9849.arm.regionkind.RegionKind;
import net.alex9849.arm.regions.Region;
import net.alex9849.arm.regions.price.Autoprice.AutoPrice;
import net.alex9849.arm.util.Saveable;
import net.alex9849.inter.WGRegion;
import net.alex9849.signs.SignData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Preset implements Saveable {
    protected String name = "default";
    protected boolean hasPrice = false;
    protected double price = 0;
    protected RegionKind regionKind = RegionKind.DEFAULT;
    protected boolean inactivityReset = true;
    protected boolean isHotel = false;
    protected boolean autoRestore = true;
    protected boolean isUserRestorable = true;
    protected int allowedSubregions = 0;
    protected FlagGroup flagGroup = FlagGroup.DEFAULT;
    protected AutoPrice autoPrice;
    protected EntityLimitGroup entityLimitGroup;
    protected List<String> setupCommands = new ArrayList<>();
    private boolean needsSave = false;

    public Preset(String name, boolean hasPrice, double price, RegionKind regionKind, FlagGroup flagGroup, boolean inactivityReset, boolean isHotel, boolean autoRestore, boolean isUserRestorable, int allowedSubregions, AutoPrice autoPrice, EntityLimitGroup entityLimitGroup, List<String> setupCommands) {
        this.name = name;
        this.hasPrice = hasPrice;
        this.price = price;
        this.regionKind = regionKind;
        this.inactivityReset = inactivityReset;
        this.isHotel = isHotel;
        this.autoRestore = autoRestore;
        this.isUserRestorable = isUserRestorable;
        this.allowedSubregions = allowedSubregions;
        this.setupCommands = setupCommands;
        this.autoPrice = autoPrice;
        this.flagGroup = flagGroup;
        this.entityLimitGroup = entityLimitGroup;
        this.needsSave = false;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FlagGroup getFlagGroup() {
        return this.flagGroup;
    }

    public void setFlagGroup(FlagGroup flagGroup) {
        this.flagGroup = flagGroup;
    }

    public boolean isUserRestorable() {
        return this.isUserRestorable;
    }

    public void setUserRestorable(boolean isUserRestorable) {
        this.isUserRestorable = isUserRestorable;
    }

    public int getAllowedSubregions() {
        return this.allowedSubregions;
    }

    public void setAllowedSubregions(int allowedSubregions) {
        this.allowedSubregions = allowedSubregions;
    }

    public void addCommand(String command) {
        this.setupCommands.add(command);
    }

    public void addCommand(List<String> command) {
        this.setupCommands.addAll(command);
    }

    public List<String> getCommands() {
        return this.setupCommands;
    }

    public void executeSavedCommands(CommandSender sender, Region region) {
        for (String command : this.setupCommands) {
            String cmd = region.getConvertedMessage(command);

            if (sender instanceof Player) {
                ((Player) sender).performCommand(cmd);
            } else {
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
            }
        }
    }

    public void removeCommand(int index) {

        if (index < 0) {
            return;
        }

        if (this.setupCommands.size() > index) {
            this.setupCommands.remove(index);
        }
    }

    public void removeAutoPrice() {
        this.autoPrice = null;
    }

    public boolean hasAutoPrice() {
        return this.autoPrice != null;
    }

    public AutoPrice getAutoPrice() {
        return this.autoPrice;
    }

    public void setAutoPrice(AutoPrice autoPrice) {
        this.autoPrice = autoPrice;
        this.removePrice();
    }

    public void getPresetInfo(Player player) {
        String price = "not defined";
        String autoPrice = Messages.convertYesNo(this.hasAutoPrice());
        if (this.hasPrice()) {
            price = this.getPrice() + "";
        }
        if (this.hasAutoPrice()) {
            autoPrice = this.getAutoPrice().getName();
        }
        RegionKind regKind = this.getRegionKind();

        player.sendMessage(ChatColor.GOLD + "=========[Preset INFO]=========");
        player.sendMessage(Messages.REGION_INFO_AUTOPRICE + autoPrice);
        player.sendMessage(Messages.REGION_INFO_PRICE + price);
        this.getAdditionalInfo(player);
        player.sendMessage(Messages.REGION_INFO_TYPE + regKind.getName());
        player.sendMessage(Messages.REGION_INFO_FLAGGROUP + flagGroup.getName());
        player.sendMessage(Messages.REGION_INFO_ENTITYLIMITGROUP + entityLimitGroup.getName());
        player.sendMessage(Messages.REGION_INFO_INACTIVITYRESET + this.isInactivityReset());
        player.sendMessage(Messages.REGION_INFO_HOTEL + this.isHotel());
        player.sendMessage(Messages.REGION_INFO_AUTORESTORE + this.isAutoRestore());
        player.sendMessage(Messages.REGION_INFO_IS_USER_RESTORABLE + this.isUserRestorable());
        player.sendMessage(Messages.REGION_INFO_ALLOWED_SUBREGIONS + this.getAllowedSubregions());
        player.sendMessage(Messages.PRESET_SETUP_COMMANDS);
        for (int i = 0; i < this.setupCommands.size(); i++) {
            String message = (i + 1) + ". /" + this.setupCommands.get(i);
            player.sendMessage(ChatColor.GOLD + message);
        }
    }

    public abstract void getAdditionalInfo(Player player);

    public void removePrice() {
        this.hasPrice = false;
        this.price = 0;
    }

    public boolean hasPrice() {
        return hasPrice;
    }

    public EntityLimitGroup getEntityLimitGroup() {
        return this.entityLimitGroup;
    }

    public void setEntityLimitGroup(EntityLimitGroup entityLimitGroup) {
        this.entityLimitGroup = entityLimitGroup;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            price = price * (-1);
        }
        this.hasPrice = true;
        this.price = price;
        this.removeAutoPrice();
    }

    public RegionKind getRegionKind() {
        return regionKind;
    }

    public void setRegionKind(RegionKind regionKind) {
        if (regionKind == null) {
            regionKind = RegionKind.DEFAULT;
        }
        this.regionKind = regionKind;
    }

    public boolean isInactivityReset() {
        return this.inactivityReset;
    }

    public void setInactivityReset(Boolean InactivityReset) {
        this.inactivityReset = InactivityReset;
    }

    public boolean isAutoRestore() {
        return autoRestore;
    }

    public void setAutoRestore(Boolean bool) {
        this.autoRestore = bool;
    }

    public boolean isHotel() {
        return isHotel;
    }

    public void setHotel(Boolean isHotel) {
        this.isHotel = isHotel;
    }

    public abstract PresetType getPresetType();

    public abstract Preset getCopy();

    public abstract boolean canPriceLineBeLetEmpty();

    /**
     * Generates a region with the settings of the preset
     * Does not create a schematic! Does not apply flaggroups!
     *
     * @param wgRegion The WorldGuard region
     * @param world    The world of the WorldGuard region
     * @param sender   The sender that executes the saved commands
     * @param signs    The signs that should be lonked to the region
     * @return
     */
    public Region generateRegion(WGRegion wgRegion, World world, CommandSender sender, List<SignData> signs) {
        Region region = generateRegion(wgRegion, world, signs);
        this.executeSavedCommands(sender, region);
        return region;
    }

    /**
     * Generates a region with the settings of the preset
     * without executing the saved commands of the preset.
     * Does not create a schematic! Does not apply flaggroups!
     *
     * @param wgRegion The WorldGuard region
     * @param world    The world of the WorldGuard region
     * @param signs    The signs that should be lonked to the region
     * @return
     */
    public abstract Region generateRegion(WGRegion wgRegion, World world, List<SignData> signs);

    @Override
    public ConfigurationSection toConfigurationSection() {
        ConfigurationSection section = new YamlConfiguration();
        section.set("hasPrice", this.hasPrice());
        section.set("price", this.getPrice());
        section.set("userrestorable", this.isUserRestorable());
        section.set("allowedSubregions", this.getAllowedSubregions());
        section.set("regionKind", this.getRegionKind().getName());
        section.set("isHotel", this.isHotel());
        section.set("autorestore", this.isAutoRestore());
        section.set("flaggroup", this.flagGroup.getName());
        section.set("entityLimitGroup", this.getEntityLimitGroup().getName());
        section.set("inactivityReset", this.isInactivityReset());
        if (this.hasAutoPrice()) {
            section.set("autoPrice", this.getAutoPrice().getName());
        } else {
            section.set("autoPrice", null);
        }
        section.set("setupcommands", this.getCommands());
        return section;
    }

    @Override
    public void queueSave() {
        this.needsSave = true;
    }

    @Override
    public void setSaved() {
        this.needsSave = false;
    }

    @Override
    public boolean needsSave() {
        return this.needsSave;
    }
}
