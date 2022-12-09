from django.shortcuts import render
from django.http import HttpResponse
# Create your views here.
from django.views.generic import ListView, DetailView
from rest_framework import viewsets, status
from rest_framework.decorators import api_view
from rest_framework.response import Response

from .models import Location
from .serializer import LocationSerializer

def location_list(request):
    return render(request, 'location_list.html')

def person_location(request, pk):
    return render(request, 'person_location.html')

class LocationViewSet(viewsets.ModelViewSet):
    queryset = Location.objects.all()
    serializer_class = LocationSerializer

@api_view(['PUT'])
def location_change(request, pk):
    now_employee = ""
    try:
        now_employee = Location.objects.get(employee_id=pk)
    except now_employee.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    serializer = LocationSerializer(Location, data=request.data)
    if serializer.is_valid():
        serializer.save()
        return Response(status.HTTP_400_BAD_REQUEST)
