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
}
