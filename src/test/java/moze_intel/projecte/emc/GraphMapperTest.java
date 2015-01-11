package moze_intel.projecte.emc;

import moze_intel.projecte.emc.arithmetics.LongArithmetic;
import org.junit.Rule;
import org.junit.rules.Timeout;
import scala.Int;

import java.util.*;

import static org.junit.Assert.*;

public class GraphMapperTest {

    @Rule
    public Timeout timeout = new Timeout(3000);

    @org.junit.Test
    public void testGetOrCreateList() throws Exception {
        Map<String,List<Integer>> map = new HashMap<String, List<Integer>>();
        List<Integer> l1 = GraphMapper.getOrCreateList(map, "abc");
        assertNotNull(l1);
        assertTrue(map.containsKey("abc"));
        List<Integer> l2 = GraphMapper.getOrCreateList(map, "abc");
        assertSame(l1, l2);
    }

    @org.junit.Test
    public void testGenerateValuesSimple() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1",1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
        graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(4, getValue(values, "c4"));

    }

    @org.junit.Test
    public void testGenerateValuesSimpleMultiRecipe() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1",1L, GraphMapper.FixedValue.FixAndInherit);
        //2 Recipes for c4
        graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
        graphMapper.addConversion(2, "c4", Arrays.asList("b2","b2"));
        graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(2, getValue(values,"c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
    }

    @org.junit.Test
    public void testGenerateValuesSimpleMultiRecipeWithEmptyAlternative() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1",1L, GraphMapper.FixedValue.FixAndInherit);
        //2 Recipes for c4
        graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
        graphMapper.addConversion(1, "c4", new LinkedList<String>());
        graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(4, getValue(values,"c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
    }

    @org.junit.Test
    public void testGenerateValuesSimpleFixedAfterInherit() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1",1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
        graphMapper.addConversion(1, "b2", Arrays.asList("a1","a1"));
        graphMapper.setValue("b2", 20L, GraphMapper.FixedValue.FixAfterInherit);

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(20, getValue(values,"b2"));
        assertEquals(4, getValue(values,"c4"));
    }

    @org.junit.Test
    public void testGenerateValuesSimpleFixedDoNotInherit() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1",1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "b2", Arrays.asList("a1","a1"));
        graphMapper.addConversion(1, "c4", Arrays.asList("b2","b2"));
        graphMapper.setValue("b2", 20L, GraphMapper.FixedValue.FixAndDoNotInherit);

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(20, getValue(values,"b2"));
        assertEquals(0, getValue(values,"c4"));
    }

    @org.junit.Test
    public void testGenerateValuesSimpleFixedDoNotInheritMultiRecipes() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1",1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "c",  Arrays.asList("a1", "a1"));
        graphMapper.addConversion(1, "c",  Arrays.asList("a1", "b"));
        graphMapper.setValue("b", 20L, GraphMapper.FixedValue.FixAndDoNotInherit);

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(20, getValue(values,"b"));
        assertEquals(2, getValue(values,"c"));
    }

    @org.junit.Test
    public void testGenerateValuesSimpleSelectMinValue() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1",1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("b2", 2L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "c", Arrays.asList("a1","a1"));
        graphMapper.addConversion(1, "c", Arrays.asList("b2", "b2"));

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(2, getValue(values,"c"));
    }

    @org.junit.Test
    public void testGenerateValuesSimpleSelectMinValueWithDependency() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1",1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("b2",2L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "c", Arrays.asList("a1","a1"));
        graphMapper.addConversion(1, "c", Arrays.asList("b2","b2"));
        graphMapper.addConversion(1, "d", Arrays.asList("c","c"));

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(2, getValue(values,"c"));
        assertEquals(4, getValue(values,"d"));
    }

    @org.junit.Test
    public void testGenerateValuesSimpleWoodToWorkBench() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("planks",1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(4, "planks", Arrays.asList("wood"));
        graphMapper.addConversion(1, "workbench", Arrays.asList("planks","planks","planks","planks"));

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(0,getValue(values,"wood"));
        assertEquals(1, getValue(values,"planks"));
        assertEquals(4, getValue(values,"workbench"));
    }

    @org.junit.Test
    public void testGenerateValuesWood() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        for (char i: "ABCD".toCharArray()) {
            graphMapper.setValue("wood" + i, 32L, GraphMapper.FixedValue.FixAndInherit);
            graphMapper.addConversion(4, "planks"+i, Arrays.asList("wood"+i));
        }

        for (char i: "ABCD".toCharArray()) {
            graphMapper.addConversion(4, "planks"+i, Arrays.asList("wood"));
        }

        for (char i: "ABCD".toCharArray())
            for (char j: "ABCD".toCharArray())
                graphMapper.addConversion(4,"stick",Arrays.asList("planks"+i,"planks"+j));
        graphMapper.addConversion(1, "crafting_table", Arrays.asList("planksA","planksA","planksA","planksA"));
        for (char i: "ABCD".toCharArray())
            for (char j: "ABCD".toCharArray())
                    graphMapper.addConversion(1,"wooden_hoe",Arrays.asList("stick","stick","planks"+i,"planks"+j));

        Map<String,Long> values = graphMapper.generateValues();
        for (char i: "ABCD".toCharArray())
            assertEquals(32,getValue(values,"wood"+i));
        for (char i: "ABCD".toCharArray())
            assertEquals(8, getValue(values,"planks"+i));
        assertEquals(4, getValue(values,"stick"));
        assertEquals(32, getValue(values,"crafting_table"));
        assertEquals(24, getValue(values,"wooden_hoe"));

    }

    @org.junit.Test
    public void testGenerateValuesDeepConversions() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1",1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "b1", Arrays.asList("a1"));
        graphMapper.addConversion(1, "c1", Arrays.asList("b1"));

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(1, getValue(values,"b1"));
        assertEquals(1, getValue(values,"c1"));
    }

    @org.junit.Test
    public void testGenerateValuesDeepInvalidConversion() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1", 1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "b", Arrays.asList("a1", "invalid1"));
        graphMapper.addConversion(1, "invalid1", Arrays.asList("a1", "invalid2"));
        graphMapper.addConversion(1, "invalid2", Arrays.asList("a1", "invalid3"));


        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(0, getValue(values,"b"));
        assertEquals(0, getValue(values,"invalid1"));
        assertEquals(0, getValue(values,"invalid2"));
        assertEquals(0, getValue(values,"invalid3"));
    }

    @org.junit.Test
    public void testGenerateValuesMultiRecipeDeepInvalid() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1", 1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));
        graphMapper.addConversion(1, "b2", Arrays.asList("invalid1"));
        graphMapper.addConversion(1, "invalid1", Arrays.asList("a1", "invalid2"));


        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(0, getValue(values,"invalid1"));
        assertEquals(0, getValue(values,"invalid2"));
    }

    @org.junit.Test
    public void testGenerateValuesMultiRecipesInvalidIngredient() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1", 1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));
        graphMapper.addConversion(1, "b2", Arrays.asList("invalid"));


        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(0, getValue(values,"invalid"));
    }

    @org.junit.Test
    public void testGenerateValuesCycleRecipe() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1", 1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "cycle-1", Arrays.asList("a1"));
        graphMapper.addConversion(1, "cycle-2", Arrays.asList("cycle-1"));
        graphMapper.addConversion(1, "cycle-1", Arrays.asList("cycle-2"));


        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(1, getValue(values,"cycle-1"));
        assertEquals(1, getValue(values,"cycle-2"));
    }

    @org.junit.Test
    public void testGenerateValuesBigCycleRecipe() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        graphMapper.setValue("a1", 1L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "cycle-1", Arrays.asList("a1"));
        graphMapper.addConversion(1, "cycle-2", Arrays.asList("cycle-1"));
        graphMapper.addConversion(1, "cycle-3", Arrays.asList("cycle-2"));
        graphMapper.addConversion(1, "cycle-4", Arrays.asList("cycle-3"));
        graphMapper.addConversion(1, "cycle-5", Arrays.asList("cycle-4"));
        graphMapper.addConversion(1, "cycle-1", Arrays.asList("cycle-5"));


        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(1, getValue(values,"cycle-1"));
        assertEquals(1, getValue(values,"cycle-2"));
        assertEquals(1, getValue(values,"cycle-3"));
        assertEquals(1, getValue(values,"cycle-4"));
        assertEquals(1, getValue(values,"cycle-5"));
    }

    @org.junit.Test
    public void testGenerateValuesFuelAndMatter() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;
        final String coal = "coal";
        final String aCoal = "alchemicalCoal";
        final String aCoalBlock = "alchemicalCoalBlock";
        final String mFuel = "mobiusFuel";
        final String mFuelBlock = "mobiusFuelBlock";
        final String aFuel = "aeternalisFuel";
        final String aFuelBlock = "aeternalisFuelBlock";
        String repeat;

        graphMapper.setValue(coal, 128L, GraphMapper.FixedValue.FixAndInherit);

        graphMapper.addConversion(1, aCoal, Arrays.asList(coal, coal, coal, coal));
        graphMapper.addConversion(4, aCoal , Arrays.asList(mFuel));
        graphMapper.addConversion(9, aCoal, Arrays.asList(aCoalBlock));
         repeat=aCoal;
        graphMapper.addConversion(1, aCoalBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

        graphMapper.addConversion(1, mFuel, Arrays.asList(aCoal, aCoal, aCoal, aCoal));
        graphMapper.addConversion(4, mFuel , Arrays.asList(aFuel));
        graphMapper.addConversion(9, mFuel, Arrays.asList(mFuelBlock));
        repeat=mFuel;
        graphMapper.addConversion(1, mFuelBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

        graphMapper.addConversion(1, aFuel, Arrays.asList(mFuel, mFuel, mFuel, mFuel));
        graphMapper.addConversion(9, aFuel, Arrays.asList(aFuelBlock));
        repeat=aFuel;
        graphMapper.addConversion(1, aFuelBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

        graphMapper.setValue("diamondBlock", 73728L, GraphMapper.FixedValue.FixAndInherit);
        final String dMatter = "darkMatter";
        final String dMatterBlock = "darkMatterBlock";

        graphMapper.addConversion(1, dMatter, Arrays.asList(aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, "diamondBlock"));
        graphMapper.addConversion(1, dMatter, Arrays.asList(dMatterBlock));
        graphMapper.addConversion(4, dMatterBlock, Arrays.asList(dMatter, dMatter, dMatter, dMatter));

        final String rMatter = "redMatter";
        final String rMatterBlock = "redMatterBlock";
        graphMapper.addConversion(1, rMatter, Arrays.asList(aFuel, aFuel, aFuel, dMatter, dMatter, dMatter, aFuel, aFuel, aFuel));
        graphMapper.addConversion(1, rMatter, Arrays.asList(rMatterBlock));
        graphMapper.addConversion(4, rMatterBlock, Arrays.asList(rMatter, rMatter, rMatter, rMatter));


        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(128, getValue(values,coal));
        assertEquals(512, getValue(values,aCoal));
        assertEquals(4608, getValue(values,aCoalBlock));
        assertEquals(2048, getValue(values,mFuel));
        assertEquals(18432, getValue(values,mFuelBlock));
        assertEquals(8192, getValue(values,aFuel));
        assertEquals(73728, getValue(values,aFuelBlock));
        assertEquals(73728, getValue(values,"diamondBlock"));
        assertEquals(139264, getValue(values, dMatter));
        assertEquals(139264, getValue(values, dMatterBlock));
        assertEquals(466944, getValue(values, rMatter));
        assertEquals(466944, getValue(values, rMatterBlock));
    }

    @org.junit.Test
    public void testGenerateValuesWool() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;

        final String[] dyes = new String[]{"Blue", "Brown", "White", "Other"};
        final long[] dyeValue = new long[] {864L, 176L, 48L, 16L};
        for (int i = 0; i < dyes.length; i++) {
            graphMapper.setValue("dye"+dyes[i], dyeValue[i], GraphMapper.FixedValue.FixAndInherit);
            graphMapper.addConversion(1, "wool" + dyes[i], Arrays.asList("woolWhite", "dye"+dyes[i]));
        }
        graphMapper.setValue("string", 12L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "woolWhite", Arrays.asList("string", "string", "string", "string"));

        graphMapper.setValue("stick", 4L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("plank", 8L, GraphMapper.FixedValue.FixAndInherit);
        for (String dye: dyes) {
            graphMapper.addConversion(1,"bed", Arrays.asList("plank","plank","plank", "wool"+dye,"wool"+dye,"wool"+dye));
            graphMapper.addConversion(3,"carpet"+dye, Arrays.asList("wool"+dye,"wool"+dye));
            graphMapper.addConversion(1,"painting", Arrays.asList("wool"+dye, "stick","stick","stick","stick","stick","stick","stick","stick"));
        }

        Map<String,Long> values = graphMapper.generateValues();
        for (int i = 0; i < dyes.length; i++) {
            assertEquals(dyeValue[i], getValue(values,"dye"+dyes[i]));
        }
        assertEquals(12, getValue(values,"string"));
        assertEquals(48, getValue(values,"woolWhite"));
        assertEquals(224, getValue(values,"woolBrown"));
        assertEquals(912, getValue(values,"woolBlue"));
        assertEquals(64, getValue(values,"woolOther"));

        assertEquals(32, getValue(values,"carpetWhite"));
        assertEquals(149, getValue(values,"carpetBrown"));
        assertEquals(608, getValue(values,"carpetBlue"));
        assertEquals(42, getValue(values,"carpetOther"));

        assertEquals(168, getValue(values,"bed"));
        assertEquals(80, getValue(values,"painting"));
    }

    @org.junit.Test
    public void testGenerateValuesBucketRecipe() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;
        graphMapper.setValue("somethingElse", 9L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("container", 23L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("fluid", 17L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "filledContainer", Arrays.asList("container", "fluid"));

        //Recipe that only consumes fluid:
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("container", -1);
        map.put("filledContainer", 1);
        map.put("somethingElse", 2);
        graphMapper.addConversionMultiple(1, "fluidCraft", map);

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(9, getValue(values,"somethingElse"));
        assertEquals(23, getValue(values,"container"));
        assertEquals(17, getValue(values,"fluid"));
        assertEquals(17+23, getValue(values,"filledContainer"));
        assertEquals(17+2*9, getValue(values,"fluidCraft"));

    }

    @org.junit.Test
    public void testGenerateValuesWaterBucketRecipe() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;
        graphMapper.setValue("somethingElse", 9L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("container", 23L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("fluid", Long.MIN_VALUE, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "filledContainer", Arrays.asList("container", "fluid"));

        //Recipe that only consumes fluid:
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("container", -1);
        map.put("filledContainer", 1);
        map.put("somethingElse", 2);
        graphMapper.addConversionMultiple(1, "fluidCraft", map);

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(9, getValue(values,"somethingElse"));
        assertEquals(23, getValue(values,"container"));
        assertEquals(0, getValue(values,"fluid"));
        assertEquals(23, getValue(values,"filledContainer"));
        assertEquals(2*9, getValue(values,"fluidCraft"));

    }

    @org.junit.Test
    public void testGenerateValuesCycleRecipeExploit() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;
        graphMapper.setValue("a1", 1L, GraphMapper.FixedValue.FixAndInherit);
        //Exploitable Cycle Recype
        graphMapper.addConversion(1, "exploitable", Arrays.asList("a1"));
        graphMapper.addConversion(2, "exploitable", Arrays.asList("exploitable"));

        //Not-exploitable Cycle Recype
        graphMapper.addConversion(1, "notExploitable", Arrays.asList("a1"));
        graphMapper.addConversion(2, "notExploitable", Arrays.asList("notExploitable", "notExploitable"));

        Map<String,Long> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(0, getValue(values,"exploitable"));
        assertEquals(1, getValue(values,"notExploitable"));
    }

    @org.junit.Test
    public void testGenerateValuesCoalToFireChargeWithWildcard() throws Exception {
        GraphMapper<String, Long> graphMapper = new GraphMapper<String, Long>(new LongArithmetic());;
        String[] logTypes = new String[]{"logA", "logB", "logC"};
        String[] log2Types = new String[]{"log2A", "log2B", "log2C"};
        String[] coalTypes = new String[]{"coal0", "coal1"};

        graphMapper.setValue("coalore", 0L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("coal0", 128L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("gunpowder", 192L, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("blazepowder", 768L, GraphMapper.FixedValue.FixAndInherit);

        for (String logType: logTypes) {
            graphMapper.setValue(logType, 32L, GraphMapper.FixedValue.FixAndInherit);
            graphMapper.addConversion(1, "log*", Arrays.asList(logType));
        }
        for (String log2Type: log2Types) {
            graphMapper.setValue(log2Type, 32L, GraphMapper.FixedValue.FixAndInherit);
            graphMapper.addConversion(1, "log2*", Arrays.asList(log2Type));
        }
        graphMapper.addConversion(1, "coal1", Arrays.asList("log*"));
        for (String coalType: coalTypes) {
            graphMapper.addConversion(1, "coal*", Arrays.asList(coalType));
            graphMapper.addConversion(3, "firecharge", Arrays.asList(coalType, "gunpowder", "blazepowder"));
        }
        graphMapper.addConversion(1, "firecharge*", Arrays.asList("firecharge"));
        Map<String, Integer> m = new HashMap<String, Integer>();
        m.put("coal0", 9);
        graphMapper.addConversionMultiple(1, "coalblock", m);

        m.clear();
        //Philosophers stone smelting 7xCoalOre -> 7xCoal
        m.put("coalore", 7);
        m.put("coal*", 1);
        graphMapper.addConversionMultiple(7, "coal0", m);

        m.clear();
        //Philosophers stone smelting logs
        m.put("log*", 7);
        m.put("coal*", 1);
        graphMapper.addConversionMultiple(7, "coal1", m);

        m.clear();
        //Philosophers stone smelting log2s
        m.put("log2*", 7);
        m.put("coal*", 1);
        graphMapper.addConversionMultiple(7, "coal1", m);


        //Smelting single coal ore
        graphMapper.addConversion(1, "coal0", Arrays.asList("coalore"));
        //Coal Block
        graphMapper.addConversion(9, "coal0", Arrays.asList("coalblock"));

        Map<String,Long> values = graphMapper.generateValues();
        for (String logType: logTypes) {
            assertEquals(32, getValue(values, logType));
        }
        assertEquals(32, getValue(values,"log*"));
        assertEquals(128, getValue(values,"coal0"));
        assertEquals(32, getValue(values,"coal1"));
        assertEquals(32, getValue(values,"coal*"));
        assertEquals(330, getValue(values,"firecharge"));
    }

    private static <T,V extends Number> int getValue(Map<T,V> map, T key) {
        V val = map.get(key);
        if (val == null) return 0;
        return val.intValue();
    }
}
