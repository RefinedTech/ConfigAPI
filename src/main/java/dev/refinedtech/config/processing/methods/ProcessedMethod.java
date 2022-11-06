package dev.refinedtech.config.processing.methods;

import dev.refinedtech.config.processing.methods.info.MethodInfo;

import java.util.List;

public record ProcessedMethod(List<String> comments, MethodInfo info) {

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int offset) {
        StringBuilder sb = new StringBuilder("  ".repeat(offset));
        sb.append("Processed Method:\n");

        String basicOffset = "  ".repeat(offset + 1);
        if (!this.comments.isEmpty()) {
            sb.append(basicOffset).append("Comments:\n");
            this.comments.forEach(comment -> sb.append(basicOffset)
                                               .append("  # ")
                                               .append(comment)
                                               .append('\n'));
            sb.append('\n');
        }
        sb.append(basicOffset).append("Method Info:\n");
        sb.append(basicOffset).append("  Method Type: ").append(this.info.getType()).append('\n');
        sb.append(basicOffset).append("  Method Name: ").append(this.info.getName()).append('\n');
        sb.append(basicOffset).append("  Method Path: ").append(String.join(".", this.info.getPathNames())).append('\n');
        return sb.toString();
    }
}
