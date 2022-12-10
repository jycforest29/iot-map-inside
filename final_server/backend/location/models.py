from django.db import models

# Create your models here.
class Location(models.Model):
    employee_id = models.CharField(max_length = 40 ,primary_key=True, unique = True)
    name = models.CharField(max_length = 40)
    location = models.CharField(max_length = 40)
