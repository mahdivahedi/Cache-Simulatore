import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    static double totalRequests = 0;

    static double instAccess = 0;
    static double dataAccess = 0;
    static int instMiss = 0;
    static int dataMiss = 0;
    static int replace = 0;

    static int demandFetch = 0;
    static int copiesBack = 0;

    private static final String FIRST_TITLE = "***CACHE SETTINGS***";
    private static final String SECOND_TITLE = "***CACHE STATISTICS***\nINSTRUCTIONS\n";

    private static final int READ_DATA = 0;
    private static final int WRITE_DATA = 1;
    private static final int READ_INSTRUCTION = 2;

    public static String CACHE_TYPE;

    private static int BLOCK_SIZE;
    private static int CACHE_SIZE;
    private static int INSTRUCTION_SIZE;
    private static int ASSOCIATIVITY;

    public static String WRITE_POLICY;
    public static String ALLOCATE_POLICY;

    public static LinkedList<String> orders = new LinkedList<>();

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        prepareFirstPhase(input);

        Cache cache = new Cache(ASSOCIATIVITY, CACHE_SIZE, INSTRUCTION_SIZE);
//        Scanner scanner = new Scanner(System.in);

        String address;
        String tag;
        int operation;
        int setNumber;
        String line;
        int tmp = orders.size();

        for (int i = 0; i < tmp; i++) {
            // Process each line.
            line = orders.pop();
            operation = Integer.parseInt(String.valueOf(line.charAt(0)));

            if (operation == 2) {
                instAccess++;
            } else {
                dataAccess++;
            }

            address = String.valueOf(Integer.parseInt(line.split(" ")[1], 16));

            // Calculate new values.
            tag = String.valueOf(Integer.parseInt(address) / BLOCK_SIZE);
//            tag = address.divide(BigInteger.valueOf(BLOCK_SIZE)); // tag = address / BLOCK_SIZE
            setNumber = Integer.parseInt(tag) % cache.numSets;
//            setNumber = (tag.mod(BigInteger.valueOf(cache.numSets))).intValue(); // setNumber = tag % numSet
            totalRequests++;

            // Check the operation.
            switch (operation) {
                case READ_DATA:
                    cache.read(tag, setNumber);
                    break;

                case WRITE_DATA:
                    cache.write(tag, setNumber);
                    break;

                case READ_INSTRUCTION:
                    cache.readInst(tag, setNumber);
                    break;

                default:
                    System.out.println("Error! Invalid operation.");
            }
        }

        printResults();

    }

    static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.####");
        return Double.parseDouble(twoDForm.format(d));
    }

    private static void printResults() {

        printFirstPhase(CACHE_TYPE, String.valueOf(CACHE_SIZE), INSTRUCTION_SIZE, String.valueOf(ASSOCIATIVITY),
                String.valueOf(BLOCK_SIZE), String.valueOf(WRITE_POLICY), ALLOCATE_POLICY);

        String secondPhase = SECOND_TITLE;
        if (INSTRUCTION_SIZE >= 1) {
            secondPhase = secondPhase + "accesses: " + (int) instAccess + "\n" +
                    "misses: " + instMiss + "\n" +
                    "miss rate: " + roundTwoDecimals(instMiss / instAccess) + " (hit rate " + (roundTwoDecimals(1 - instMiss / instAccess)) + ")\n" +
                    "replace: " + replace + "\n";
        } else {
            secondPhase = secondPhase + "accesses: " + 0 + "\n" +
                    "misses: " + 0 + "\n" +
                    "miss rate: " + "0.0000" + " (hit rate 0.0000)\n" +
                    "replace: " + 0 + "\n";
        }
        secondPhase = secondPhase + "DATA\n" +
                "accesses: " + (int) dataAccess + "\n" +
                "misses: " + dataMiss + "\n" +
                "miss rate: " + roundTwoDecimals(dataMiss / dataAccess) + " (hit rate " + (roundTwoDecimals(1 - dataMiss / dataAccess)) + ")\n" +
                "replace: " + replace + "\n" +
                "TRAFFIC (in words)\n" +
                "demand fetch: " + demandFetch + "\n" +
                "copies back: " + copiesBack;

        System.out.println(secondPhase);
    }

    private static void prepareFirstPhase(Scanner input) {

        String[] lineFirst = input.nextLine().split(" ");
        String[] sizes = input.nextLine().split(" ");
        String line;

        while (input.hasNextLine() && !(line = input.nextLine().trim()).isEmpty()) {
            orders.addLast(line);
        }

        BLOCK_SIZE = Integer.parseInt(lineFirst[0]);
        ASSOCIATIVITY = Integer.parseInt(lineFirst[4]);

        if (sizes.length >= 2) {
            CACHE_SIZE = Integer.parseInt(sizes[2]);
            INSTRUCTION_SIZE = Integer.parseInt(sizes[0]);
        } else {
            CACHE_SIZE = Integer.parseInt(sizes[0]);
            INSTRUCTION_SIZE = 0;
        }
        boolean instructionSizeNeed;

        CACHE_TYPE = lineFirst[2].equalsIgnoreCase("0") ? "Unified I- D-cache" : "Split I- D-cache";
        instructionSizeNeed = INSTRUCTION_SIZE != 0;
        if (lineFirst[2].equalsIgnoreCase("1"))
            instructionSizeNeed = true;
        if (!instructionSizeNeed)
            INSTRUCTION_SIZE = 0;

        String writePolicy = lineFirst[6];
        String allocationPolicy = lineFirst[8];

        if (writePolicy.equalsIgnoreCase("wb")) {
            writePolicy = "WRITE BACK";
        } else {
            writePolicy = "WRITE THROUGH";
        }
        WRITE_POLICY = writePolicy;

        if (allocationPolicy.equalsIgnoreCase("wa"))
            allocationPolicy = "WRITE ALLOCATE";
        else
            allocationPolicy = "WRITE NO ALLOCATE";
        ALLOCATE_POLICY = allocationPolicy;


//        printFirstPhase(CACHE_TYPE, String.valueOf(CACHE_SIZE), instructionSizeNeed ? String.valueOf(INSTRUCTION_SIZE) : null,
//                lineFirst[4], lineFirst[0], lineFirst[6], lineFirst[8]);
    }

    private static void printFirstPhase(String cacheType, String dataSize, int instructionSize,
                                        String associativity, String blockSize, String writePolicy, String allocationPolicy) {

        String firstPhase;

        if (instructionSize != 0) {
            firstPhase = FIRST_TITLE + "\n" + cacheType + "\nI-cache size: " + instructionSize + "\nD-cache size: " + dataSize +
                    "\nAssociativity: " + associativity + "\nBlock size: " + blockSize +
                    "\nWrite policy: " + writePolicy +
                    "\nAllocation policy: " + allocationPolicy + "\n";
        } else {
            firstPhase = FIRST_TITLE + "\n" + cacheType + "\nSize: " + dataSize +
                    "\nAssociativity: " + associativity + "\nsecond.Block size: " + blockSize +
                    "\nWrite policy: " + writePolicy +
                    "\nAllocation policy: " + allocationPolicy + "\n";
        }
        System.out.println(firstPhase);

    }

    static class Block {
        String tag;
        boolean isDirty;
        boolean isEmpty;

        Block() {
            tag = "";
            isDirty = false;
            isEmpty = true;
        }
    }

    static class Cache {
        int associativity;
        int numSets;
        int size;
        int instSize;
        Block[][] dataBlocks;
        Block[][] instructionBlocks;
        ArrayList<LinkedList<Integer>> metadata;
        ArrayList<LinkedList<Integer>> metainst;

        Cache(int associativity, int cacheSize, int instructionSize) {
            this.associativity = associativity;
            size = cacheSize;
            instSize = instructionSize;
            numSets = cacheSize / (BLOCK_SIZE * associativity);
            instructionBlocks = new Block[numSets][associativity];
            dataBlocks = new Block[numSets][associativity];
            metadata = new ArrayList<>();
            metainst = new ArrayList<>();

            // Initialize blocks.
            for (int i = 0; i < dataBlocks.length; i++)
                for (int j = 0; j < dataBlocks[i].length; j++)
                    dataBlocks[i][j] = new Block();

            // Initialize blocks.
            for (int i = 0; i < instructionBlocks.length; i++)
                for (int j = 0; j < instructionBlocks[i].length; j++)
                    instructionBlocks[i][j] = new Block();

            // Initialize metadata.
            for (int i = 0; i < numSets; i++)
                metadata.add(new LinkedList<>());

            // Initialize metadata.
            for (int i = 0; i < numSets; i++)
                metainst.add(new LinkedList<>());
        }

        // Finds a free cache block to be used.
        int getFreeBlock(int setNumber) {
            LinkedList<Integer> set = metadata.get(setNumber);

            // Check if there is a free block.
            for (int i = 0; i < associativity; i++)
                if (dataBlocks[setNumber][i].isEmpty)
                    return i;

            return set.remove();
        }

        int getFreeBlockInst(int setNumber) {
            LinkedList<Integer> set = metainst.get(setNumber);

            // Check if there is a free block.
            for (int i = 0; i < associativity; i++)
                if (dataBlocks[setNumber][i].isEmpty)
                    return i;

            return set.remove();
        }

        // Returns the index for the given tag. Returns -1 if the tag is not in the cache.
        int indexOf(String tag, int setNumber) {
            for (int i = 0; i < associativity; i++)
                if (dataBlocks[setNumber][i].tag != null && dataBlocks[setNumber][i].tag.compareTo(tag) == 0)
                    return i;

            return -1;
        }

        int indexOfInst(String tag, int setNumber) {
            for (int i = 0; i < associativity; i++)
                if (instructionBlocks[setNumber][i].tag != null && instructionBlocks[setNumber][i].tag.compareTo(tag) == 0)
                    return i;

            return -1;
        }

        // Reads a tag from the cache in the specified set.
        void read(String  tag, int setNumber) {
            int index = indexOf(tag, setNumber);

            // Check for a hit.
            if (index != -1) {
                updateMetadata(setNumber, index);
            }

            // Check for a miss.
            else {
                dataMiss++;
                index = getFreeBlock(setNumber);
                Block block = dataBlocks[setNumber][index];

                block.tag = tag;
                block.isEmpty = false;
                demandFetch++;

                if (WRITE_POLICY.equals("WRITE BACK")) {
                    if (block.isDirty) {
                        copiesBack += BLOCK_SIZE / 4;
                    }

                    block.isDirty = false;
                } else {
                    copiesBack++;
                    block.isDirty = true;
                }

                updateMetadata(setNumber, index);
            }


        }

        void readInst(String tag, int setNumber) {
            int index = indexOfInst(tag, setNumber);

            // Check for a hit.
            if (index != -1) {
                updateMetadataInst(setNumber, index);
            }

            // Check for a miss.
            else {
                instMiss++;
                index = getFreeBlockInst(setNumber);
                Block block = instructionBlocks[setNumber][index];

                block.tag = tag;
                block.isEmpty = false;
                demandFetch++;

                updateMetadataInst(setNumber, index);
            }
        }

        // Updates the cache metadata according to the specified replacement policy.
        void updateMetadata(int setNumber, int index) {
            LinkedList<Integer> set = metadata.get(setNumber);

            // Check if the queue is empty.
            if (set.size() != 0) {

                int targetIndex = set.indexOf(index);

                if (targetIndex != -1)
                    set.remove(targetIndex);


            }
            set.add(index);
        }

        void updateMetadataInst(int setNumber, int index) {
            LinkedList<Integer> set = metainst.get(setNumber);

            // Check if the queue is empty.
            if (set.size() != 0) {

                int targetIndex = set.indexOf(index);

                if (targetIndex != -1)
                    set.remove(targetIndex);


            }
            set.add(index);
        }

        // Writes a tag to to the cache in the specified set.
        void write(String  tag, int setNumber) {
            Block block;
            int index = indexOf(tag, setNumber);

            // Check for a hit.
            if (index != -1) {
                block = dataBlocks[setNumber][index];

                block.tag = tag;
                block.isEmpty = false;

                // Check the replacement policy.
                switch (WRITE_POLICY) {
                    case "WRITE THROUGH":
                        copiesBack++;
                        break;

                    case "WRITE BACK":
                        block.isDirty = true;
                        break;
                }

                updateMetadata(setNumber, index);
            }

            // Check for a miss.
            else {
                dataMiss++;
                index = getFreeBlock(setNumber);
                block = dataBlocks[setNumber][index];
                block.tag = tag;
                block.isEmpty = false;

                // Check the replacement policy.
                switch (WRITE_POLICY) {
                    case "WRITE THROUGH":
                        copiesBack++;
                        break;

                    case "WRITE BACK":
                        if (block.isDirty) {
                            copiesBack += BLOCK_SIZE / 4;
                        }
                        dataBlocks[setNumber][index].isDirty = true;
                        break;
                }

                updateMetadata(setNumber, index);
            }
        }
    }
}
