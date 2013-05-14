package funativity.age.opengl.shaders;

/**
 * Provides access to a few hardcoded shaders for use in lighting.
 *
 */
public class CommonCode
{
	//@formatter:off
	
	/**
	 * Provides code (intended for a fragment shader) that has a DirectionalLight struct. This struct holds information
	 * for a directional light; color, ambient, strength, and direction. Also a method is provided that calculates
	 * the light called getDirectionalLightColor. It takes a DirectionalLight struct instance, and a vec3 of the normal
	 * for the current fragment. 
	 * <BR><BR><BR>
	 * <B>Definitions:</B>
	 * <BR>
	 * struct DirectionalLight<BR>
	 * {<BR>
	 * 		vec3 color;<BR>
	 * 		float ambient;<BR>
	 * 		float strength;<BR>
	 * 		vec3 direction<BR>
	 * }
	 * <BR><BR>
	 * vec4 getDirectionalLightColor( DirectionalLight dirLight, vec3 normal);
	 * 
	 */
	public static final String DIRECTIONAL_LIGHT = "" +
		"struct DirectionalLight																				\n" +
		"{																										\n" +
		"	vec3 color;																							\n" +
		"	float ambient;																						\n" +
		"	float strength;																						\n" +
		"	vec3 direction;																						\n" +
		"};																										\n" +
		"																										\n" +
		"vec4 getDirectionalLightColor( DirectionalLight dirLight, vec3 normal )								\n" +
		"{																										\n" +
		"	float diffuseIntensity = max( 0.0, dot( normal, -dirLight.direction ) );							\n" +
		"	return vec4( dirLight.color * ( dirLight.ambient + diffuseIntensity ) * dirLight.strength, 1.0 );	\n" +
		"}																										\n";

	
	/**
	 * Simple fragment shader that uses directional lighting. Expects texture coordinates, and a normal vector to be
	 * provided. Also has a uniform called 'color' that needs to be set. This color uniform is combined with the 
	 * texture color.
	 * 
	 * <BR><BR><BR>
	 * <B>Definitions:</B>
	 * <BR>
	 * varying vec2 texCoord;<BR>
	 * varying vec3 normal;<BR>
	 * uniform sampler2D sampler;<BR>
	 * uniform vec3 color;<BR>
	 * uniform DirectionalLight dirLight;<BR>
	 */
	public static final String FRAGMENT_DIRECTIONALLIGHTING = "" +
		"precision mediump float;																				\n" +
		"																										\n" +
		"varying vec2 texCoord;																					\n" +
		"varying vec3 normal;																					\n" +
		"																										\n" +
		"uniform sampler2D sampler;																				\n" +
		"uniform vec3 color;																					\n" +
		"																										\n" +
		DIRECTIONAL_LIGHT +
		"																										\n" +
		"uniform DirectionalLight dirLight;																		\n" +
		"																										\n" +
		"void main()																							\n" +
		"{																										\n" +
		"	vec3 normalized = normalize( normal );																\n" +
		"	vec4 mtlColor = vec4( color, 1.0 ) * texture2D( sampler, texCoord );								\n" +
		"	vec4 lightColor = getDirectionalLightColor( dirLight, normalized );									\n" +
		"	gl_FragColor = vec4( mtlColor.xyz, 1.0 ) * lightColor;												\n" +
		"}";
	
	//@formatter:on
}
