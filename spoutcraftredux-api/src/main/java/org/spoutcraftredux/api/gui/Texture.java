package org.spoutcraftredux.api.gui;

import org.spoutcraftredux.api.asset.Asset;

/**
 * An image registered with {@link org.spoutcraftredux.api.asset.AssetRegistry}, stretched to the widget's size. Give
 * it a width and height; it has no natural size.
 */
public final class Texture extends Widget<Texture> {
    private Asset asset;

    /**
     * Creates a texture.
     *
     * @param asset the image
     */
    public Texture(final Asset asset) {
        this.asset = asset;
    }

    /**
     * Gets the image.
     *
     * @return the image
     */
    public Asset getAsset() {
        return this.asset;
    }

    /**
     * Sets the image.
     *
     * @param asset the image
     * @return this texture
     */
    public Texture setAsset(final Asset asset) {
        this.asset = asset;
        this.changed();
        return this;
    }
}
