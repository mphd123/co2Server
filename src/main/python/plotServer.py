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
        print(f"[Python] got request for plot ",flush=True)
        X = request.x
        Y = request.y

        fig, ax = plt.subplots(figsize=(8, 4))
        ax.plot(X,Y,marker='o', linestyle='-', color='tab:blue')
        ax.set_xlabel('Time')
        ax.set_ylabel('Co2')
        ax.grid(True)

        fig.tight_layout()

        buf = io.BytesIO()

        fig.savefig(buf, format='png')
        plt.close(fig)
        buf.seek(0)
        image_bytes = buf.read()
        print(f"[Python] Generated image bytes size: {len(image_bytes)}",flush=True)

        return image_pb2.PlotResponse(image=image_bytes)

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    image_pb2_grpc.add_PlotServiceServicer_to_server(PlotServiceServicer(), server)
    server.add_insecure_port(f"{acceptedAddresses}:{port}")
    server.start()
    server.wait_for_termination()

if __name__ == '__main__':
    serve()