from django.views.generic import TemplateView
from django.shortcuts import render
from django.template import loader
from django.http import HttpResponse, JsonResponse
# Create your views here.
from django.views.generic import ListView, DetailView
from location.models import Location
import json
from django.core import serializers
def home(request):

    context = {

    }
    return render(request, 'home.html', context=context)
    #return HttpResponse("hello world")

def bluetooth(request):
    d1 = int(request.POST.get('d1'))
    d2 = int(request.POST.get('d2'))

    row = 100
    col = 100

    candidate = [[1 for c in range(10)] for r in range(10)]

    bluetooth1 = [0,5]
    bluetooth2 = [10,5]

    row_step = row / 10
    col_step = col / 10

    for r in range(10):
        for c in range(10):
            now_distance = (abs(r-bluetooth1[0]) * row_step) ** 2 + (abs(c-bluetooth1[1]) * col_step) ** 2

            if (now_distance <  d1 * d1 * 0.75) or (now_distance > d1 * d1 * 1.25):
                candidate[r][c] = 0

    for r in range(10):
        for c in range(10):
            if candidate[r][c] == 0 : continue

            now_distance = (abs(r-bluetooth2[0]) * row_step) ** 2 + (abs(c-bluetooth2[1]) * col_step) ** 2

            if (now_distance <  d2 * d2 * 0.75) or (now_distance > d2 * d2 * 1.25):
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

    x_result = (sum_of_r/num_candidate) * row_step
    y_result = (sum_of_c/num_candidate) * col_step

    result = {}
    result['x'] = x_result
    result['y'] = y_result

    return JsonResponse(result)
    