import { ipcRenderer } from "electron";

(() => {
  const canvas = document.querySelector("#canvas") as HTMLCanvasElement;
  const ctx = canvas?.getContext("2d");

  const resize = () => {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    draw();
  };

  const target = {
    x: canvas.width / 2,
    y: canvas.height / 2,
  };

  const draw = () => {
    ctx?.clearRect(0, 0, canvas.width, canvas.height);
    ctx?.beginPath();
    ctx?.arc(target.x, target.y, 10, 0, Math.PI * 2);
    ctx?.stroke();
    ctx?.closePath();
  };

  ipcRenderer.send("zap-start");
  ipcRenderer.on("zap-acc-data", (_, data) => {
    target.x += -data.x;
    target.y += data.y;
    draw();
  });

  window.addEventListener("resize", resize);

  resize();
})();
