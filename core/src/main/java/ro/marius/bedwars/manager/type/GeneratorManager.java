package ro.marius.bedwars.manager.type;

import ro.marius.bedwars.BedWarsPlugin;

public class GeneratorManager {

    private String DFirstText;
    private String DSecondText;
    private String DThirdText;
    private int DTimeTierOne;
    private int DTimeTierTwo;
    private int DTimeTierThree;
    private String EFirstText;
    private String ESecondText;
    private String EThirdText;
    private int ETimeTierOne;
    private int ETimeTierTwo;
    private int ETimeTierThree;

    public GeneratorManager(BedWarsPlugin plugin) {

        this.DFirstText = plugin.getConfig().getString("DiamondGenerator.FirstTier.Text");
        this.DTimeTierOne = plugin.getConfig().getInt("DiamondGenerator.FirstTier.SpawnTime");
        this.DSecondText = plugin.getConfig().getString("DiamondGenerator.SecondTier.Text");
        this.DTimeTierTwo = plugin.getConfig().getInt("DiamondGenerator.SecondTier.SpawnTime");
        this.DThirdText = plugin.getConfig().getString("DiamondGenerator.ThirdTier.Text");
        this.DTimeTierThree = plugin.getConfig().getInt("DiamondGenerator.ThirdTier.SpawnTime");

        this.EFirstText = plugin.getConfig().getString("EmeraldGenerator.FirstTier.Text");
        this.ESecondText = plugin.getConfig().getString("EmeraldGenerator.FirstTier.Text");
        this.EThirdText = plugin.getConfig().getString("EmeraldGenerator.SecondTier.Text");
        this.ETimeTierOne = plugin.getConfig().getInt("EmeraldGenerator.SecondTier.Text");
        this.ETimeTierTwo = plugin.getConfig().getInt("EmeraldGenerator.ThirdTier.Text");
        this.ETimeTierThree = plugin.getConfig().getInt("EmeraldGenerator.ThirdTier.Text");

    }

    public String getDFirstText() {
        return this.DFirstText;
    }

    public void setDFirstText(String dFirstText) {
        this.DFirstText = dFirstText;
    }

    public String getDSecondText() {
        return this.DSecondText;
    }

    public void setDSecondText(String dSecondText) {
        this.DSecondText = dSecondText;
    }

    public String getDThirdText() {
        return this.DThirdText;
    }

    public void setDThirdText(String dThirdText) {
        this.DThirdText = dThirdText;
    }

    public int getDTimeTierOne() {
        return this.DTimeTierOne;
    }

    public void setDTimeTierOne(int dTimeTierOne) {
        this.DTimeTierOne = dTimeTierOne;
    }

    public int getDTimeTierTwo() {
        return this.DTimeTierTwo;
    }

    public void setDTimeTierTwo(int dTimeTierTwo) {
        this.DTimeTierTwo = dTimeTierTwo;
    }

    public int getDTimeTierThree() {
        return this.DTimeTierThree;
    }

    public void setDTimeTierThree(int dTimeTierThree) {
        this.DTimeTierThree = dTimeTierThree;
    }

    public String getEFirstText() {
        return this.EFirstText;
    }

    public void setEFirstText(String eFirstText) {
        this.EFirstText = eFirstText;
    }

    public String getESecondText() {
        return this.ESecondText;
    }

    public void setESecondText(String eSecondText) {
        this.ESecondText = eSecondText;
    }

    public String getEThirdText() {
        return this.EThirdText;
    }

    public void setEThirdText(String eThirdText) {
        this.EThirdText = eThirdText;
    }

    public int getETimeTierOne() {
        return this.ETimeTierOne;
    }

    public void setETimeTierOne(int eTimeTierOne) {
        this.ETimeTierOne = eTimeTierOne;
    }

    public int getETimeTierTwo() {
        return this.ETimeTierTwo;
    }

    public void setETimeTierTwo(int eTimeTierTwo) {
        this.ETimeTierTwo = eTimeTierTwo;
    }

    public int getETimeTierThree() {
        return this.ETimeTierThree;
    }

    public void setETimeTierThree(int eTimeTierThree) {
        this.ETimeTierThree = eTimeTierThree;
    }

}
