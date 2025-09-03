package pos.machine;

import java.util.List;

public class PosMachine {
    // ========== Step 1. 数据库加载 ==========
    private Map<String, Item> loadItemDatabase() {
        return ItemsLoader.loadAllItems().stream().collect(Collectors.toMap(Item::getBarcode, item -> item));
    }

    // ========== Step 2. 校验 ==========
    private boolean validateBarcodes(List<String> barcodes) {
        Set<String> validBarcodes = loadItemDatabase().keySet();
        return barcodes.stream().allMatch(validBarcodes::contains);
    }

    // ========== Step 3. 统计数量 ==========
    private Map<String, Integer> countBarcodes(List<String> barcodes) {
        return barcodes.stream()
                .collect(Collectors.toMap(code -> code, code -> 1, Integer::sum));
    }

    // ========== Step 4. 匹配商品 ==========
    private List<ReceiptItem> matchItems(Map<String, Integer> barcodeCount) {
        Map<String, Item> itemDatabase = loadItemDatabase();
        return barcodeCount.entrySet()
                .stream()
                .map(entry -> {
                    Item item = itemDatabase.get(entry.getKey());
                    return new ReceiptItem(item.getName(), entry.getValue(), item.getPrice());
                })
                .collect(Collectors.toList());
    }

}
