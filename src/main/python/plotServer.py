import matplotlib

matplotlib.use('Agg')

import grpc
from concurrent import futures
import image_pb2, image_pb2_grpc
import matplotlib.pyplot as plt
import numpy as np
import io

import argparse

parser = argparse.ArgumentParser()
parser.add_argument('-port', type=int, default=50051)
args = parser.parse_args()

port = args.port
acceptedAddresses = '127.0.0.1' #  use [::] for all interfaces



print(f"[Python] Starting server on port {port}",flush=True)


class PlotServiceServicer(image_pb2_grpc.PlotServiceServicer):
    def GeneratePlot(self, request, context):
        try:
            print(f"[Python] got request for plot", flush=True)
            X = [ts.ToDatetime() for ts in request.x]
            Y = request.y
            print(f"[Python] received X length: {len(X)}, Y length: {len(Y)}", flush=True)

            fig, ax = plt.subplots(figsize=(8, 4))
            print("[Python] created figure", flush=True)

            ax.plot(X,Y,marker='o', linestyle='-', color='tab:blue')
            ax.set_xlabel('Time')
            ax.set_ylabel('Co2')
            ax.grid(True)
            print("[Python] plotted data", flush=True)

            fig.tight_layout()
            print("[Python] adjusted layout", flush=True)

            buf = io.BytesIO()
            fig.savefig(buf, format='png')
            print("[Python] saved figure to buffer", flush=True)

            plt.close(fig)
            print("[Python] closed figure", flush=True)

            buf.seek(0)
            image_bytes = buf.read()
            print(f"[Python] Generated image bytes size: {len(image_bytes)}", flush=True)

            return image_pb2.PlotResponse(image=image_bytes)

        except Exception as e:
            import traceback
            traceback.print_exc()
            context.set_details(f"Exception: {str(e)}")
            context.set_code(grpc.StatusCode.INTERNAL)
            return image_pb2.PlotResponse()  # or raise grpc.RpcError if you prefer

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    image_pb2_grpc.add_PlotServiceServicer_to_server(PlotServiceServicer(), server)
    server.add_insecure_port(f"{acceptedAddresses}:{port}")
    server.start()
    server.wait_for_termination()

if __name__ == '__main__':
    serve()