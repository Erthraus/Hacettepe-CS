document.addEventListener('DOMContentLoaded', main);

async function loadShaderFile(url) {
    const response = await fetch(url);
    const shaderText = await response.text();
    return shaderText;
}

function compileShader(gl, source, type) {
    const shader = gl.createShader(type);
    gl.shaderSource(shader, source);
    gl.compileShader(shader);

    const success = gl.getShaderParameter(shader, gl.COMPILE_STATUS);
    if(!success) {
        console.error(`Shader compilation failed(${type === gl.VERTEX_SHADER ? 'VERTEX' : 'FRAGMENT'}):`, gl.getShaderInfoLog(shader));
        gl.deleteShader(shader);
        return null;
    }

    return shader;
}

function createProgram(gl, vertexShader, fragmentShader) {
    const program = gl.createProgram();
    gl.attachShader(program, vertexShader);
    gl.attachShader(program, fragmentShader);
    gl.linkProgram(program);

    const success = gl.getProgramParameter(program, gl.LINK_STATUS);

    if(!success) {
        console.error('Program linking failed:', gl.getProgramInfoLog(program));
        gl.deleteProgram(program);
        return null;
    }

    return program;
}

async function main() {
    const {mat4} = glMatrix;
    const canvas = document.getElementById('webgl-canvas');
    if(!canvas) {
        console.error('Canvas element not found');
        return;
    }

    const gl = canvas.getContext('webgl2');
    if(!gl) {
        console.error('WebGL2 not supported in this browser');
        return;
    }

    const vertexShaderSource = await loadShaderFile('vertex.glsl');
    const fragmentShaderSource = await loadShaderFile('fragment.glsl');

    const vertexShader = compileShader(gl, vertexShaderSource, gl.VERTEX_SHADER);
    const fragmentShader = compileShader(gl, fragmentShaderSource, gl.FRAGMENT_SHADER);

    if(!vertexShader || !fragmentShader) {
        return;
    }

    const program = createProgram(gl, vertexShader, fragmentShader);
    if(!program) {
        return;
    }

    gl.deleteShader(vertexShader);
    gl.deleteShader(fragmentShader);

    const colorUniformLocation = gl.getUniformLocation(program, 'u_color');
    if(!colorUniformLocation) {
        console.error('Could not find uniform location for u_color');
        return;
    }

    const projectionMatrixUniformLocation = gl.getUniformLocation(program, "u_projectionMatrix");
    const modelViewMatrixUniformLocation = gl.getUniformLocation(program, "u_modelViewMatrix");

    const projectionMatrix = mat4.create();
    const fieldOfView = 45 * Math.PI / 180;
    const aspect = gl.canvas.clientWidth / gl.canvas.clientHeight;
    const zNear = 0.1;
    const zFar = 100.0;
    mat4.perspective(projectionMatrix, fieldOfView, aspect, zNear, zFar);

    function drawScene(time) {
        const greenValue = (Math.sin(time / 1000) + 1) / 2;
        const currentRotationInRadians = time * 0.005;
        const modelViewMatrix = mat4.create();
        mat4.translate(modelViewMatrix,
                       modelViewMatrix,
                       [0.0, 0.0, -2.5]);
        mat4.rotateY(modelViewMatrix, modelViewMatrix, currentRotationInRadians);

        gl.viewport(0, 0, canvas.width, canvas.height);
        gl.clearColor(0.0, 0.0, 0.0, 1.0);
        gl.clear(gl.COLOR_BUFFER_BIT);

        gl.useProgram(program);
        gl.uniform4fv(colorUniformLocation, [0.0, greenValue, 0.0, 1.0]);
        gl.uniformMatrix4fv(projectionMatrixUniformLocation, false, projectionMatrix);
        gl.uniformMatrix4fv(modelViewMatrixUniformLocation, false, modelViewMatrix);
        gl.drawArrays(gl.LINE_LOOP, 0, numSides * 3);

        requestAnimationFrame(drawScene);
    }

    const vertices = [];
    const numSides = 8;
    const radius = 0.5;

    for (let i = 0; i < numSides; i++) {
    const angle1 = (i / numSides) * Math.PI * 2;
    const x1 = radius * Math.cos(angle1);
    const y1 = radius * Math.sin(angle1);

    const angle2 = ((i + 1) / numSides) * Math.PI * 2;
    const x2 = radius * Math.cos(angle2);
    const y2 = radius * Math.sin(angle2);

    // Her üçgeni (Merkez, Köşe 1, Köşe 2) olarak açıkça tanımla
    vertices.push(0.0, 0.0, 0.0); // Merkez
    vertices.push(x1, y1, 0.0);   // Köşe 1
    vertices.push(x2, y2, 0.0);   // Köşe 2
}

    const vertexBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, vertexBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vertices), gl.STATIC_DRAW);

    const positionAttributeLocation = gl.getAttribLocation(program, 'a_position');
    gl.enableVertexAttribArray(positionAttributeLocation);
    gl.vertexAttribPointer(positionAttributeLocation, 3, gl.FLOAT, false, 0, 0);

    requestAnimationFrame(drawScene);
}