package com.projectdgdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.math.Vector3;
import com.projectdgdx.game.gameobjects.GameObject;
import com.projectdgdx.game.renderer.BaseShader;
import com.projectdgdx.game.utils.AssetManager;
import com.projectdgdx.game.utils.AssetsFinder;
import com.projectdgdx.game.utils.Map;

import com.badlogic.gdx.utils.Array;
import com.projectdgdx.game.utils.MapParser;

import java.util.Random;

public class ProjectD extends ApplicationAdapter {

    private FPSLogger fps;

    private final float moveSpeed = 0.2f;

    private PerspectiveCamera cam;
    private CameraInputController camController;
    private ModelBatch modelBatch;
    private ModelBatch shadowBatch;
    public Array<ModelInstance> instances = new Array<ModelInstance>();
    public Array<AnimationController> animationControllers = new Array<AnimationController>();
    private AnimationController animController;

    public Environment environment;
    DirectionalShadowLight shadowLight;
    public boolean loading;
    private Model floor;

    public RenderContext renderContext;
    public Shader shader;

    private Model animatedModel;
    private ModelInstance animatedInstance;

    public Renderable renderable;

    Random rand;
    Map map;

    private int playerIndex;

    @Override
    public void create () {
        MapParser parser = new MapParser();
        map = parser.parse("BasicMapTest");
        rand = new Random();

        loadAssets();
        createEnvironment();
        createCamera();

        shader = new BaseShader();
        shader.init();

        modelBatch = new ModelBatch();

        fps = new FPSLogger();
    }

    public void loadAssets(){
        modelBatch = new ModelBatch();
        shadowBatch = new ModelBatch(new DepthShaderProvider());
        //model
        AssetManager.loadModel("animRobot.g3dj");
        AssetManager.loadModel("machineAO.g3dj");
        AssetManager.loadModel("ship.g3db");

        loading = true;
    }

    private void doneLoading() {

       //ModelInstance playerInstance;
       //playerInstance = new ModelInstance(AssetManager.getModel("robo.g3dj"));
       //playerInstance.transform.setToTranslation(0, 0, 0);
       //playerInstance.transform.scale(0.2f, 0.2f, 0.2f);

       // NodePart blockPart = playerInstance.getNode("robo_root").getChild(0).parts.get(0);
       // renderable = new Renderable();
       // blockPart.setRenderable(renderable);
       // renderable.environment = null;
       // renderable.worldTransform.idt();

       //renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
       //instances.add(playerInstance);

        for (GameObject gameObject : map.getGameObjects()) {
            ModelInstance npcInstance;
            System.out.println(AssetsFinder.getModelPath(gameObject.getId()));
            npcInstance = new ModelInstance(AssetManager.getModel(AssetsFinder.getModelPath(gameObject.getId())));
            npcInstance.transform.setToTranslation(gameObject.getPosition());
            Vector3 scale = gameObject.getScale();
            npcInstance.transform.scale(scale.x, scale.y, scale.z);
            Vector3 rotation = gameObject.getRotation();
            npcInstance.transform.rotate(Vector3.X, rotation.x);
            npcInstance.transform.rotate(Vector3.Y, rotation.y);
            npcInstance.transform.rotate(Vector3.Z, rotation.z);

            if(gameObject.getId() == "control.basic"){
                environment.add(new PointLight().set(0.3f, 0.9f, 0.3f,
                        gameObject.getPosition().x, 15f, gameObject.getPosition().z, 100f));}



            if(gameObject.getId() == "worker.basic" || gameObject.getId() == "player.basic") {




                animController = new AnimationController(npcInstance);
                animController.setAnimation("IdleAnim", -1, new AnimationController.AnimationListener() {
                    @Override
                    public void onEnd(AnimationController.AnimationDesc animation) {
                    }

                    @Override
                    public void onLoop(AnimationController.AnimationDesc animation) {
                     //   Gdx.app.log("INFO", "Animation Ended");
                    }
                });
                animationControllers.add(animController);
            }
            instances.add(npcInstance);
        }

        //Create a temp floor
        ModelBuilder modelBuilder = new ModelBuilder();
        floor = modelBuilder.createBox(500f, 1f, 500f,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instances.add(new ModelInstance(floor));


        loading = false;

        shader = new BaseShader();
        shader.init();
    }

    public void createEnvironment(){
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.4f, 0.4f, 1f));
        environment.add((shadowLight = new DirectionalShadowLight(4048, 4048, 100f, 100f, 0.1f, 1500f)).set(0.8f, 0.7f, 0.6f, -1f, -.4f,
                -.2f));
        environment.shadowMap = shadowLight;



        environment.add(new PointLight().set(0.9f, 0.3f, 0.3f,
                35, 15f, 45f, 100f));
       // for(int i =0; i < 10; i++){
       //     environment.add(new PointLight().set(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(),
       //             rand.nextFloat()*10, rand.nextFloat()*10, rand.nextFloat()*10, 0.4f));
       // }

    }


    public void createCamera(){
        cam = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 55f, 35f);
        cam.lookAt(0f, 0f, 0f);
        cam.near = 0.01f;
        cam.far = 1000f;
        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

    }


    public void render () {

        fps.log();

        if (loading && AssetManager.update())
            doneLoading();
        cam.update();

        shadowLight.begin(Vector3.Zero, cam.direction);
        shadowBatch.begin(shadowLight.getCamera());


        for (ModelInstance instance : instances) {
            shadowBatch.render(instance, environment);
        }

        shadowBatch.end();
        shadowLight.end();

        for(AnimationController controllerInstance: animationControllers){
            controllerInstance.update(Gdx.graphics.getDeltaTime() + rand.nextFloat() * 0.02f);
        }


        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
                (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        //renderable.meshPart.primitiveType = GL20.GL_LINE_STRIP;

        if(!loading)
            moveModel(instances.get(2));

        modelBatch.begin(cam);
        for (ModelInstance instance : instances) {
            modelBatch.render(instance, environment);
        }

        modelBatch.end();
    }

    private void moveModel(ModelInstance instance){

        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            instance.transform.trn(moveSpeed, 0, 0);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            instance.transform.trn(-moveSpeed, 0, 0);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            instance.transform.trn(0, 0, -moveSpeed);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            instance.transform.trn(0, 0, moveSpeed);
        }


    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();
        AssetManager.dispose();
    }
}
