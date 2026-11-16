package org.spoutcraftredux.api.asset;

import net.kyori.adventure.key.Key;

/**
 * An image registered with {@link AssetRegistry}. Clients download it once and cache it by hash.
 *
 * @param id     the id it was registered under
 * @param sha256 the SHA-256 of the file, as lowercase hex
 * @param size   the file size in bytes
 */
public record Asset(Key id, String sha256, int size) {
}
