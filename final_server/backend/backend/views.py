from django.views.generic import TemplateView
from django.shortcuts import render
from django.template import loader
from rest_framework.views import APIView
from django.http import HttpResponse, JsonResponse
# Create your views here.
from django.views.generic import ListView, DetailView
from location.models import Location
from rest_framework.response import Response
# from httpx import Stat
from backend.serializers import RequestSerializer
import json
# from django.core import serializers

def home(request):

    context = {

    }
    # return render(request, 'home.html', context=context)
    return JsonResponse(context)


# def bluetooth(request):

#     d1 = int(request.POST.get('d1'))
#     d2 = int(request.POST.get('d2'))

#     row = 100
#     col = 100

#     candidate = [[1 for c in range(10)] for r in range(10)]

#     bluetooth1 = [0,5]
#     bluetooth2 = [10,5]

#     row_step = row / 10
#     col_step = col / 10

#     for r in range(10):
#         for c in range(10):
#             now_distance = (abs(r-bluetooth1[0]) * row_step) ** 2 + (abs(c-bluetooth1[1]) * col_step) ** 2

#             if (now_distance <  d1 * d1 * 0.75) or (now_distance > d1 * d1 * 1.25):
#                 candidate[r][c] = 0

#     for r in range(10):
#         for c in range(10):
#             if candidate[r][c] == 0 : continue

#             now_distance = (abs(r-bluetooth2[0]) * row_step) ** 2 + (abs(c-bluetooth2[1]) * col_step) ** 2

#             if (now_distance <  d2 * d2 * 0.75) or (now_distance > d2 * d2 * 1.25):
#                 candidate[r][c] = 0
    
#     num_candidate = 0
#     sum_of_r = 0
#     sum_of_c = 0

#     for r in range(10):
#         for c in range(10):
#             if candidate[r][c] == 1:
#                 num_candidate += 1
#                 sum_of_r += r
#                 sum_of_c += c

#     x_result = (sum_of_r/num_candidate) * row_step
#     y_result = (sum_of_c/num_candidate) * col_step

#     result = {}
#     result['x'] = x_result
#     result['y'] = y_result

#     return JsonResponse(result)

prev_x = 1
prev_y = 1

class BlueToothAPIVIEW(APIView):
    
    def post(self, request):
        global prev_x
        global prev_y

        # request가 정상적인지 확인
        serializer = RequestSerializer(data = request.data)
        if serializer.is_valid():
            d1 = int(serializer.data.get('d1'))
            d2 = int(serializer.data.get('d2'))
            print(d1)
            print(d2)
            power1 = (-59-d1) / (10 * 2)
            power2 = (-59-d2) / (10 * 2)
            print(power1)
            print(power2)

            d1 = 10 ** power1
            d2 = 10 ** power2

            print(d1, end = " ")
            print(d2)

            #row = 732 #cm
            #col = 1209 # cm
            row= 7.32
            col = 12.09
            candidate = [[1 for c in range(10)] for r in range(10)]

            bluetooth1 = [0,5]
            bluetooth2 = [10,5]

            row_step = row / 10
            col_step = col / 10

            for r in range(10):
                for c in range(10):
                    now_distance = (abs(r-bluetooth1[0]) * row_step) ** 2 + (abs(c-bluetooth1[1]) * col_step) ** 2

                    if (now_distance <  d1 * d1 * 0.6) or (now_distance > d1 * d1 * 1.4):
                        candidate[r][c] = 0

            for r in range(10):
                for c in range(10):
                    if candidate[r][c] == 0 : continue

                    now_distance = (abs(r-bluetooth2[0]) * row_step) ** 2 + (abs(c-bluetooth2[1]) * col_step) ** 2

                    if (now_distance <  d2 * d2 * 0.6) or (now_distance > d2 * d2 * 1.4):
                        candidate[r][c] = 0
            
            num_candidate = 0
            sum_of_r = 0
            sum_of_c = 0

            for r in range(10):
                for c in range(10):
                    if candidate[r][c] == 1:
                        num_candidate += 1
                        sum_of_r += r
                        sum_of_c += c

            if num_candidate != 0 :
                x_result = (sum_of_r/num_candidate) * row_step
                y_result = (sum_of_c/num_candidate) * col_step
                prev_x = x_result
                prev_y = y_result
            else:
                x_result = prev_x
                y_result = prev_y

            result = {}
            #x_result = 1.464
            #y_result = 6.045

            x_result *= 100
            x_result = int(x_result)
            x_result -= 662
            # x_result = -516
            if x_result < -660:
                x_result = -660

            y_result *= 100
            y_result = int(y_result)
            y_result -= 245
            if y_result < -245:
                y_result = -245

            result['x'] = x_result
            result['y'] = y_result
            print(x_result)
            print(y_result)
            
            """
            { 
                "x": -516,
                "y": 359
            }
            """

            return Response(result, status=200)

            
