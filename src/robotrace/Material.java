package robotrace;

/**
 * Materials that can be used for the robots.
 */
public enum Material {

    /**
     * Gold material properties. 
     */
    GOLD(
            new float[]{232f / 255f, 197f / 255f, 0f / 255f, 1.0f},
            new float[]{1f, 1f, 1f, 1.0f},
            100f),
    /**
     * Silver material properties. 
     */
    SILVER(
            new float[]{192f / 255f, 192f / 255f, 192f / 255f, 1.0f},
            new float[]{1f, 1f, 1f, 1.0f},
            100f),
    /**
     * Wood material properties. 
     */
    WOOD(
            new float[]{139f / 255f, 115f / 255f, 85f / 255f, 1.0f},
            new float[]{0.0f, 0.0f, 0.0f, 1.0f},
            0f),
    /**
     * Orange material properties. 
     */
    ORANGE(
            new float[]{255f / 255f, 140f / 255f, 0f / 255f, 1.0f},
            new float[]{25f/255f, 25f/255f, 25f/255f, 1.0f},
            100f);

    /**
     * The diffuse RGBA reflectance of the material.
     */
    float[] diffuse;

    /**
     * The specular RGBA reflectance of the material.
     */
    float[] specular;

    /**
     * The specular exponent of the material.
     */
    float shininess;

    /**
     * Constructs a new material with diffuse and specular properties.
     */
    private Material(float[] diffuse, float[] specular, float shininess) {
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }
}
