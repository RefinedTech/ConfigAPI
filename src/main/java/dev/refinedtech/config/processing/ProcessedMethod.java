package dev.refinedtech.config.processing;

import dev.refinedtech.config.processing.info.MethodInfo;

import java.util.List;

public record ProcessedMethod(List<String> comments, MethodInfo info) {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Processed Method:\n");
        if (!this.comments.isEmpty()) {
            sb.append("  Comments:\n");
            this.comments.forEach(comment -> sb.append("    # ").append(comment).append('\n'));
        }
        sb.append('\n');
        sb.append("  Method Info:\n");
        sb.append("    Method Type: ").append(this.info.getType()).append('\n');
        sb.append("    Method Name: ").append(this.info.getName()).append('\n');
        sb.append("    Method Path: ").append(String.join(".", this.info.getPathNames())).append('\n');
        return sb.toString();
    }
}
